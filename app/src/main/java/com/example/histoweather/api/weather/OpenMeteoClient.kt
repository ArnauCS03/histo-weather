package com.example.histoweather.api.weather

import android.util.Log
import com.example.histoweather.api.OpenMeteoParameters
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.Throws
import kotlin.math.max
import kotlin.math.min


/**
 * Data object automatically filled by ktor from the JSON response
 */
@Serializable
data class OpenMeteoResponse(
    @SerialName("latitude")
    val latitude: String,

    @SerialName("longitude")
    val longitude: String,

    @SerialName("current")
    val current: Map<String, String>? = null,

    @SerialName("hourly")
    val hourly: Map<String, List<String>>? = null,

    @SerialName("daily")
    val daily: Map<String, List<String>>? = null
)

/**
 * Weather Client Interface for testing purposes
 */
interface WeatherClient {
    var hourlyRange: Pair<Long, Long>
    var dailyRange: Pair<Long, Long>
    fun addHourlyRange(range: Pair<Long, Long>)
    fun addDailyRange(range: Pair<Long, Long>)
    fun addHourlyRequirement(requirement: String): Boolean
    fun addHourlyRequirements(requirementsList: List<String>): MutableList<String>
    fun addDailyRequirement(requirement: String): Boolean
    fun addDailyRequirements(requirementsList: List<String>): MutableList<String>
    fun addCurrentRequirement(requirement: String): Boolean
    fun addCurrentRequirements(requirementsList: List<String>): MutableList<String>
    fun setMetric()
    fun setImperial()
    suspend fun getRange(latitude: Double, longitude: Double, date: LocalDate): PlaceWeatherData
}

class OpenMeteoClient(): WeatherClient {
    // both ranges in days
    override var hourlyRange: Pair<Long, Long> = Pair(Long.MAX_VALUE, Long.MIN_VALUE)
    override var dailyRange: Pair<Long, Long> = Pair(Long.MAX_VALUE, Long.MIN_VALUE)

    // forecast and historical requirements are now distinct
    val forecastHourlyRequirements: MutableList<String> = mutableListOf()
    val historicalHourlyRequirements: MutableList<String> = mutableListOf()
    val forecastDailyRequirements: MutableList<String> = mutableListOf()
    val historicalDailyRequirements: MutableList<String> = mutableListOf()
    val currentRequirements: MutableList<String> = mutableListOf()

    val histEnd = LocalDate.now().minusDays(5)
    var unitString: String = ""

    /**
     * Checks if the given range contains historical data
     */
    fun containsHistoricalRange(start: LocalDate, end: LocalDate): Boolean {
        return start <= histEnd
    }

    /**
     * Checks if the given range contains forecast data
     */
    fun containsForecastRange(start: LocalDate, end: LocalDate): Boolean {
        return end > histEnd
    }

    /**
     * Returns the range of days that are historical by truncating the given range
     */
    fun toHistRange(start: LocalDate, end: LocalDate): Pair<LocalDate, LocalDate> {
        if (start > end) throw IllegalArgumentException("Start date must be before end date")
        if (start > histEnd) throw IllegalArgumentException("Start date must be historical")

        val startDate = start
        val endDate = minOf(histEnd, end)
        return Pair(startDate, endDate)
    }

    /**
     * Returns the range of days that are forecast by truncating the given range
     */
    fun toForecastRange(start: LocalDate, end: LocalDate): Pair<LocalDate, LocalDate> {
        if (start > end) throw IllegalArgumentException("Start date must be before end date")
        if (end < histEnd) throw IllegalArgumentException("End date must be in the future")

        val startDate = maxOf(histEnd.plusDays(1), start)
        val endDate = end
        return Pair(startDate, endDate)
    }

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
     * There is a distinction between forecast and historical API calls
     * Therefore, the parameters are put in the respective lists
     */
    override fun addHourlyRequirement(requirement: String): Boolean {
        var success = false
        if (OpenMeteoParameters.Companion.forecastHourlyParameters.contains(requirement)) {
            if (!forecastHourlyRequirements.contains(requirement))
                forecastHourlyRequirements.add(requirement)
            success = true
        }
        if (OpenMeteoParameters.Companion.historicalHourlyParameters.contains(requirement)) {
            if (!historicalHourlyRequirements.contains(requirement))
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
     * There is a distinction between forecast and historical API calls
     * Therefore, the parameters are put in the respective lists
     */
    override fun addDailyRequirement(requirement: String): Boolean {
        var success = false
        if (OpenMeteoParameters.Companion.forecastDailyParameters.contains(requirement)) {
            if (!forecastDailyRequirements.contains(requirement))
                forecastDailyRequirements.add(requirement)
            success = true
        }
        if (OpenMeteoParameters.Companion.historicalDailyParameters.contains(requirement)) {
            if (!historicalDailyRequirements.contains(requirement))
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
            if (!currentRequirements.contains(requirement))
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
        unitString = "&temperature_unit=fahrenheit&wind_speed_unit=mph&precipitation_unit=inch"
    }

    /**
     * sets all units to metric
     * temperature: Celsius
     * wind speed: km/h
     * precipitation: mm
     * in the future, this could be more granular
     */
    override fun setMetric() {
        unitString = "&temperature_unit=celsius&wind_speed_unit=kmh&precipitation_unit=mm"
    }

    /**
     * HTTP client for fetching data from the OpenMeteo API
     */
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Constructs a URL for fetching the current weather
     * using all parameters given earlier
     */
    fun constructCurrentURL(latitude: Double, longitude: Double): String {
        var callURL = OpenMeteoParameters.Companion.baseURLs["current"]
        callURL += "latitude=$latitude" +
                "&longitude=$longitude" +
                "&timezone=auto"

        callURL += "&current="
        for (param in currentRequirements) {
            callURL += "$param,"
        }

        callURL += unitString

        return callURL
    }

    /**
     * Constructs a URL for fetching the future (and slightly past)
     * hourly weather using all parameters given earlier
     */
    fun constructHourlyForecastURL(
        latitude: Double,
        longitude: Double,
        startDate: LocalDate,
        endDate: LocalDate,
    ): String {
        var callURL = OpenMeteoParameters.Companion.baseURLs["forecast"]
        callURL += "latitude=$latitude" +
                "&longitude=$longitude" +
                "&start_date=$startDate" +
                "&end_date=$endDate" +
                "&timezone=auto"

        callURL += "&hourly="
        for (param in forecastHourlyRequirements) {
            callURL += "$param,"
        }

        callURL += unitString

        return callURL
    }

    /**
     * Constructs a URL for fetching the future (and slightly past)
     * daily weather using all parameters given earlier
     */
    fun constructDailyForecastURL(
        latitude: Double,
        longitude: Double,
        startDate: LocalDate,
        endDate: LocalDate,
    ): String {
        var callURL = OpenMeteoParameters.Companion.baseURLs["forecast"]
        callURL += "latitude=$latitude" +
                "&longitude=$longitude" +
                "&start_date=$startDate" +
                "&end_date=$endDate" +
                "&timezone=auto"

        callURL += "&daily="
        for (param in forecastDailyRequirements) {
            callURL += "$param,"
        }

        callURL += unitString

        return callURL
    }

    /**
     * Constructs a URL for fetching the past hourly weather
     * using all parameters given earlier
     */
    fun constructHourlyHistURL(
        latitude: Double,
        longitude: Double,
        startDate: LocalDate,
        endDate: LocalDate,
    ): String {
        var callURL = OpenMeteoParameters.Companion.baseURLs["historical"]
        callURL += "latitude=$latitude" +
                "&longitude=$longitude" +
                "&start_date=$startDate" +
                "&end_date=$endDate" +
                "&timezone=auto"

        callURL += "&hourly="
        for (param in historicalHourlyRequirements) {
            callURL += "$param,"
        }

        callURL += unitString

        return callURL
    }

    /**
     * Constructs a URL for fetching the past daily weather
     * using all parameters given earlier
     */
    fun constructDailyHistURL(
        latitude: Double,
        longitude: Double,
        startDate: LocalDate,
        endDate: LocalDate,
    ): String {
        var callURL = OpenMeteoParameters.Companion.baseURLs["historical"]
        callURL += "latitude=$latitude" +
                "&longitude=$longitude" +
                "&start_date=$startDate" +
                "&end_date=$endDate" +
                "&timezone=auto"

        callURL += "&daily="
        for (param in historicalDailyRequirements) {
            callURL += "$param,"
        }

        callURL += unitString

        return callURL
    }

    /**
     * Calls the API for current weather.
     *
     * @returns -   Current weather data for the given place if the call is successful.
     */
    @Throws(NoTransformationFoundException::class)
    suspend fun getCurrent(
        latitude: Double,
        longitude: Double,
    ): OpenMeteoResponse {
        val callURL = constructCurrentURL(latitude, longitude)
        return httpClient.get(callURL).body()
    }

    /**
     * Calls the API for hourly weather.
     * The responsible API is automatically determined by the given range and the current date.
     *
     * @returns -   Hourly weather data for the given place if the call is successful.
     */
    @Throws(NoTransformationFoundException::class)
    suspend fun getHourly(
        latitude: Double,
        longitude: Double,
        queryDate: LocalDate,
    ): MutableList<OpenMeteoResponse> {
        val dataList: MutableList<OpenMeteoResponse> = mutableListOf()
        val startDate = queryDate.plusDays(hourlyRange.first)
        val endDate = queryDate.plusDays(hourlyRange.second)

        if (containsHistoricalRange(startDate, endDate)) {
            val histRange = toHistRange(startDate, endDate)
            val callURL =
                constructHourlyHistURL(latitude, longitude, histRange.first, histRange.second)
            dataList.add(httpClient.get(callURL).body())
        }

        if (containsForecastRange(startDate, endDate)) {
            val forecastRange = toForecastRange(startDate, endDate)
            val callURL = constructHourlyForecastURL(
                latitude,
                longitude,
                forecastRange.first,
                forecastRange.second
            )
            dataList.add(httpClient.get(callURL).body())
        }
        return dataList
    }

    /**
     * Calls the API for daily weather.
     * The responsible API is automatically determined by the given range and the current date.
     *
     * @returns -   Daily weather data for the given place if the call is successful.
     */
    @Throws(NoTransformationFoundException::class)
    suspend fun getDaily(
        latitude: Double,
        longitude: Double,
        queryDate: LocalDate,
    ): MutableList<OpenMeteoResponse> {
        val dataList: MutableList<OpenMeteoResponse> = mutableListOf()
        val startDate = queryDate.plusDays(dailyRange.first)
        val endDate = queryDate.plusDays(dailyRange.second)

        if (containsHistoricalRange(startDate, endDate)) {
            val histRange = toHistRange(startDate, endDate)
            val callURL =
                constructDailyHistURL(latitude, longitude, histRange.first, histRange.second)
            dataList.add(httpClient.get(callURL).body())
        }

        if (containsForecastRange(startDate, endDate)) {
            val forecastRange = toForecastRange(startDate, endDate)
            val callURL = constructDailyForecastURL(
                latitude,
                longitude,
                forecastRange.first,
                forecastRange.second
            )
            dataList.add(httpClient.get(callURL).body())
        }
        return dataList
    }

    /**
     * Calls the API for current weather and the given range.
     * The responsible API is automatically determined by the
     * given range and the current date.
     *
     * @returns -   Weather data for a single place, that is
     *              easy to loop through. If one call fails,
     *              empty weather data is returned.
     *              -> Won't go unnoticed and can be handled
     */
    override suspend fun getRange(
        latitude: Double,
        longitude: Double,
        queryDate: LocalDate,
    ): PlaceWeatherData {
        val dataList: MutableList<OpenMeteoResponse> = mutableListOf()

        // call current API
        try {
            if (!currentRequirements.isEmpty()) dataList.add(getCurrent(latitude, longitude))
            if (!forecastHourlyRequirements.isEmpty() && !historicalHourlyRequirements.isEmpty())
                dataList.addAll(getHourly(latitude, longitude, queryDate))
            if (!forecastDailyRequirements.isEmpty() && !historicalDailyRequirements.isEmpty())
                dataList.addAll(getDaily(latitude, longitude, queryDate))
        } catch (e: Exception) {
            Log.e("OpenMeteoClient", "Failed to fetch data: ${e.message}")
            return PlaceWeatherData()
        }

        return PlaceWeatherData(queryDate, dataList)
    }
}