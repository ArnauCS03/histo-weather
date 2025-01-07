package com.example.histoweather.test

import com.example.histoweather.api.OpenMeteoParameters
import com.example.histoweather.api.weather.OpenMeteoResponse
import com.example.histoweather.api.weather.PlaceWeatherData
import com.example.histoweather.api.weather.WeatherClient
import java.time.LocalDate
import kotlin.collections.contains
import kotlin.math.max
import kotlin.math.min

class MockClient: WeatherClient {
    // both ranges in days
    override var hourlyRange: Pair<Long, Long> = Pair(0, 0)
    override var dailyRange: Pair<Long, Long> = Pair(0, 0)

    // forecast and historical requirements are now distinct
    val forecastHourlyRequirements: MutableList<String> = mutableListOf()
    val historicalHourlyRequirements: MutableList<String> = mutableListOf()
    val forecastDailyRequirements: MutableList<String> = mutableListOf()
    val historicalDailyRequirements: MutableList<String> = mutableListOf()
    val currentRequirements: MutableList<String> = mutableListOf()

    var metric = true // default to metric

    /**
     * Broadens range of days to request hourly data from relative to the given date
     * All ranges given will be included in a single request
     */
    override fun addHourlyRange(range: Pair<Long, Long>) {
        hourlyRange = Pair(
            min(range.first, hourlyRange.first),
            max(range.second, hourlyRange.second)
        )
    }

    /**
     * Broadens range of days to request daily data from relative to the given date
     * All ranges given will be included in a single request
     */
    override fun addDailyRange(range: Pair<Long, Long>) {
        dailyRange = Pair(
            min(range.first, dailyRange.first),
            max(range.second, dailyRange.second)
        )
    }

    /**
     * Adds a parameter to consecutive API calls for hourly data
     * if it is present in OpenMeteoParameters.
     * For now, there is no distinction between forecast and historical API calls
     * Therefore, only parameters are accepted, that are present in both APIs
     */
    override fun addHourlyRequirement(requirement: String): Boolean {
        var success = false
        if (OpenMeteoParameters.Companion.forecastHourlyParameters.contains(requirement)) {
            forecastHourlyRequirements.add(requirement)
            success = true
        }
        if (OpenMeteoParameters.Companion.historicalHourlyParameters.contains(requirement)) {
            historicalHourlyRequirements.add(requirement)
            success = true
        }
        return success
    }

    /**
     * Simplifies adding parameters in bulk by accepting a List
     */
    override fun addHourlyRequirements(requirementsList: List<String>): MutableList<String> {
        val failedList: MutableList<String> = mutableListOf()
        for (requirement in requirementsList) {
            if (!addHourlyRequirement(requirement)) {
                failedList.add(requirement)
            }
        }
        return failedList
    }

    /**
     * Adds a parameter to consecutive API calls for daily data
     * if it is present in OpenMeteoParameters.
     * For now, there is no distinction between forecast and historical API calls
     * Therefore, only parameters are accepted, that are present in both APIs
     */
    override fun addDailyRequirement(requirement: String): Boolean {
        var success = false
        if (OpenMeteoParameters.Companion.forecastDailyParameters.contains(requirement)) {
            forecastDailyRequirements.add(requirement)
            success = true
        }
        if (OpenMeteoParameters.Companion.historicalDailyParameters.contains(requirement)) {
            historicalDailyRequirements.add(requirement)
            success = true
        }
        return success
    }

    /**
     * Simplifies adding parameters in bulk by accepting a List
     */
    override fun addDailyRequirements(requirementsList: List<String>): MutableList<String> {
        val failedList: MutableList<String> = mutableListOf()
        for (requirement in requirementsList) {
            if (!addDailyRequirement(requirement)) {
                failedList.add(requirement)
            }
        }
        return failedList
    }

    /**
     * Adds a parameter to consecutive API calls for current data
     * if it is present in OpenMeteoParameters.
     */
    override fun addCurrentRequirement(requirement: String): Boolean {
        if (OpenMeteoParameters.Companion.currentParameters.contains(requirement)) {
            currentRequirements.add(requirement)
            return true
        }
        return false
    }

    /**
     * Simplifies adding parameters in bulk by accepting a List
     */
    override fun addCurrentRequirements(requirementsList: List<String>): MutableList<String> {
        val failedList: MutableList<String> = mutableListOf()
        for (requirement in requirementsList) {
            if (!addCurrentRequirement(requirement)) {
                failedList.add(requirement)
            }
        }
        return failedList
    }

    /**
     * sets all units to imperial
     * temperature: Fahrenheit
     * wind speed: mph
     * precipitation: inch
     * in the future, this could be more granular
     */
    override fun setImperial() {
        metric = false
    }

    /**
     * sets all units to metric
     * temperature: Celsius
     * wind speed: km/h
     * precipitation: mm
     * in the future, this could be more granular
     */
    override fun setMetric() {
        metric = true
    }

    override suspend fun getRange(
        latitude: Double,
        longitude: Double,
        queryDate: LocalDate,
    ): PlaceWeatherData {
        val dataList: MutableList<OpenMeteoResponse> = mutableListOf()

        if (!currentRequirements.isEmpty()) dataList.add(mockCurrentResponse(latitude, longitude, queryDate))
        if (!forecastHourlyRequirements.isEmpty() && !historicalHourlyRequirements.isEmpty())
            dataList.add(mockHourlyResponse(latitude, longitude, queryDate))
        if (!forecastDailyRequirements.isEmpty() && !historicalDailyRequirements.isEmpty())
            dataList.add(mockDailyResponse(latitude, longitude, queryDate))


        return PlaceWeatherData(queryDate, dataList)
    }

    fun numHours(): Int {
        return ((hourlyRange.second - hourlyRange.first + 1) * 24).toInt()
    }

    fun numDays(): Int {
        return (dailyRange.second - dailyRange.first + 1).toInt()
    }

    fun mockCurrentResponse(): OpenMeteoResponse {
        return mockCurrentResponse(48.8566, 2.3522, LocalDate.now())
    }

    fun mockCurrentResponse(latitude: Double, longitude: Double, queryDate: LocalDate): OpenMeteoResponse {
        val currentProperties: Map<String, String> = mapOf(
            "time" to queryDate.toString()+"T12:00",
            "temperature_2m" to if(metric) "7.1" else "44.8",
            "weather_code" to "51",
            "apparent_temperature" to if(metric) "6.8" else "44.2",
        )

        return OpenMeteoResponse(
            latitude = latitude.toString(),
            longitude = longitude.toString(),
            current = currentProperties,
        )
    }

    fun mockHourlyResponse(): OpenMeteoResponse {
        return mockHourlyResponse(48.8566, 2.3522, LocalDate.now())
    }

    fun mockHourlyResponse(latitude: Double, longitude: Double, queryDate: LocalDate): OpenMeteoResponse {
        val hourlyProperties: Map<String, List<String>> = mapOf(
            // list of size numHours() containing timestamps with 1 hour intervals
            "time" to List(numHours()) { i -> queryDate.plusDays((i/24).toLong()).toString()+"T${"%02d".format(i%24)}:00" },
            "temperature_2m" to if(metric) List(numHours()) { i -> listOf("6", "6", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7", "7")[i % 24]}
                else List(numHours()) { i -> listOf("43", "43", "44", "44", "45", "45", "45", "45", "45", "45", "45", "45", "45", "45", "45", "45", "44", "44", "45", "45", "45", "44", "44", "44")[i % 24]},
            "weather_code" to List(numHours()) { i -> listOf("3", "3", "51", "51", "3", "51", "51", "3", "51", "3", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51")[i % 24] },
            "precipitation" to List(numHours()) { i -> listOf("0", "2", "0", "7", "0", "1", "1", "2", "2", "7", "0", "1", "0", "3", "5", "0", "4", "1", "0", "2", "0", "0", "0", "1")[i % 24] },
            "precipitation_probability" to List(numHours()) { i -> listOf("30", "0", "50", "90", "0", "0","30", "0", "0", "0", "0", "0","20", "40", "50", "0", "0", "0","70", "0", "50", "90", "0", "0")[i % 24] }
            )
        return OpenMeteoResponse(
            latitude = latitude.toString(),
            longitude = longitude.toString(),
            hourly = hourlyProperties,
        )
    }

    fun mockDailyResponse(): OpenMeteoResponse {
        return mockDailyResponse(48.8566, 2.3522, LocalDate.now())
    }

    fun mockDailyResponse(latitude: Double, longitude: Double, queryDate: LocalDate): OpenMeteoResponse {
        val dailyProperties: Map<String, List<String>> = mapOf(
            // list of size numDays() containing timestamps with 1 day intervals
            "time" to List(numDays()) { i -> queryDate.plusDays(i.toLong() + dailyRange.first).toString() },
            "temperature_2m_mean" to if(metric) List(numDays()) { i -> listOf(5, 7, 4, 6, 2, 1, -2, 0, 4, 4, 3, 2, 1, 1).map{ it.toString()}[i%14]}
                else List(numDays()) { i -> listOf(42, 45, 40, 44, 36, 34, 28, 33, 39, 39, 37, 35, 34, 33).map{ it.toString()}[i%14]},
            "weather_code" to List(numDays()) { i -> listOf("3", "51", "51", "51", "51", "51", "3", "85", "85", "3", "51", "1", "2", "85")[i % 14] },
            "temperature_2m_min" to List(numDays()) { if(metric) "6.0" else "42.8" },
            "temperature_2m_max" to List(numDays()) { if(metric) "7.4" else "45.4" },
            "precipitation_sum" to List(numHours()) { i -> listOf("1", "0", "5", "0", "0", "2", "3", "2", "0", "0", "0", "0", "0", "15", "0", "0", "0", "2")[i % 18] }
        )

        return OpenMeteoResponse(
            latitude = latitude.toString(),
            longitude = longitude.toString(),
            daily = dailyProperties,
        )
    }
}