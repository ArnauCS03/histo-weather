package com.example.histoweather.api.weather

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Saves the current state of the weather containing the time,
 * it was last measured, and the requested properties
 */
data class Current(
    val time: LocalDateTime,
    val properties: Map<String, String>
)

/**
 * Contains the time of the hour and all hourly properties requested
 */
data class Hour(
    val time: LocalDateTime,
    val properties: Map<String, String>
)

/**
 * Contains the date of the day ans all daily properties requested
 */
data class Day(
    val date: LocalDate,
    val properties: Map<String, String>
)

/**
 * Object containing weather data for a single place.
 * It can be created out of a "OpenMeteoResponse",
 * by concatenating multiple responses or as an empty object.
 *
 * Additional responses can be appended.
 * The order matters! There is no sorting!
 *
 * All parameters can be null or empty.
 * The valid property shows, if the object is empty
 */
class PlaceWeatherData {
    var queryDate: LocalDate
    var latitude: String? = null
    var longitude: String? = null
    var current: Current? = null
    var hourList: MutableList<Hour> = mutableListOf()
    var daysList: MutableList<Day> = mutableListOf()
    var valid: Boolean = false

    constructor() {
        this.queryDate = LocalDate.now()
    }

    /**
     * Creates PlaceWeatherData out of a single response
     */
    constructor(queryDate: LocalDate, resp: OpenMeteoResponse) {
        this.queryDate = queryDate
        appendResp(resp)
    }

    /**
     * Creates PlaceWeatherData out of a list of responses.
     * They are appended in the order of the list.
     * The list should be in order and non overlapping!
     */
    constructor(queryDate: LocalDate, respList: List<OpenMeteoResponse>) {
        this.queryDate = queryDate
        for (resp in respList) {
            appendResp(resp)
        }
    }

    /**
     * Get the hourly data for a specified range of days relative to the query date
     */
    fun getRelativeHourlyData(range: Pair<Long, Long>): List<Hour> {
        val res = hourList.filter {
            it.time.toLocalDate() in
                    queryDate.plusDays(range.first)..queryDate.plusDays(range.second)
        }

        if (res.isEmpty()) {
            throw NoSuchElementException("No data found for the specified range")
        }

        return res
    }

    /**
     * Get the daily data for a specified range of days relative to the query date
     */
    fun getRelativeDailyData(range: Pair<Long, Long>): List<Day> {
        val res = daysList.filter {
            it.date in queryDate.plusDays(range.first)..queryDate.plusDays(range.second)
        }

        if (res.isEmpty()) {
            throw NoSuchElementException("No data found for the specified range")
        }

        return res
    }

    /**
     * Get the data for a specific day
     */
    fun getDayData(date: LocalDate): Day? {
        return daysList.find { it.date == date }
    }

    /**
     * Get the data for a specific hour
     */
    fun getHourData(time: LocalDateTime): Hour? {
        return hourList.find { it.time == time }
    }

    /**
     * Appends new data from a response to the existing one.
     * The coordinates are only set once from the first response.
     * All consecutive responses should belong to the same place!
     *
     * As data is added here, the validity is checked and updated
     */
    fun appendResp(resp: OpenMeteoResponse) {
        if (latitude == null || longitude == null) {
            latitude = resp.latitude
            longitude = resp.longitude
        }

        // current
        if (resp.current != null) {
            val time = LocalDateTime.parse(resp.current["time"])
            if (current == null || current!!.time < time) {
                val prop = resp.current.toMutableMap()
                prop.remove("time")
                current = Current(time, prop)
            }
        }

        // hours
        resp.hourly?.get("time")?.forEachIndexed { index, element ->
            val time = LocalDateTime.parse(element)
            val properties: MutableMap<String, String> = mutableMapOf()
            for (prop in resp.hourly) {
                if (prop.key == "time") {
                    continue
                }
                properties[prop.key] = prop.value[index]
            }
            hourList.add(Hour(time, properties))
        }

        // days
        resp.daily?.get("time")?.forEachIndexed { index, element ->
            val date = LocalDate.parse(element)
            val properties: MutableMap<String, String> = mutableMapOf()
            for (prop in resp.daily) {
                if (prop.key == "time") {
                    continue
                }
                properties[prop.key] = prop.value[index]
            }
            daysList.add(Day(date, properties))
        }

        checkValid()
    }

    /**
     * Checks the validity of the data.
     * Data is valid, if at least one of the
     * data fields is filled with actual data.
     */
    fun checkValid(): Boolean {
        valid = current != null || !hourList.isEmpty() || !daysList.isEmpty()
        return valid
    }
}