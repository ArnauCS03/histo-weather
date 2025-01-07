package com.example.histoweather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.histoweather.ui.localDateToString
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ComposeTestRule.clickAndVerifyWithTags(clickTag: String, verifyTag: String) {
    onNodeWithTag(clickTag).performClick()
    onNodeWithTag(verifyTag).assertIsDisplayed()
}

/**
 * get a list of dates from today minus 3 days to today plus 10 days
 */
fun getDateRange(): List<LocalDate> {
    val today = LocalDate.now()
    val startDate = today.minusDays(3)
    val endDate = today.plusDays(10)

    return generateSequence(startDate) { it.plusDays(1) }
        .takeWhile { !it.isAfter(endDate) }
        .toList()
}

/**
 * Tests the hourly widget with the given data.
 * It sets the location to Karlskrona and checks if the hourly temperatures and icons are correct.
 * @param setLocation a function to set the location for a given city
 * @param setImperialUnits a function to set the units to imperial
 * @param historical_page if true, the test is for the historical page, otherwise for the home page
 */
fun ComposeTestRule.testHourlyWidget(setLocation: (String, String) -> Unit, setImperialUnits: () -> Unit, setMetricUnits: () -> Unit, historical_page: Boolean) {
    val city = "Berlin"
    val countryCode = "DE"

    val precipitation_probability = listOf("30", "0", "50", "90", "0", "0","30", "0", "0", "0", "0", "0","20", "40", "50", "0", "0", "0","70", "0", "50", "90", "0", "0")
    val perciptation = listOf("0", "2", "0", "7", "0", "1", "1", "2", "2", "7", "0", "1", "0", "3", "5", "0", "4", "1", "0", "2", "0", "0", "0", "1")
    val weatherCodes = listOf("3", "3", "51", "51", "3", "51", "51", "3", "51", "3", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51")
    val icons = weatherCodes.map { weatherCodesMap[it].toString() }

    // in Celsius
    val temps = listOf(6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)
    // in Fahrenheit
    val f_temps = listOf(43, 43, 44, 44, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 44, 44, 45, 45, 45, 44, 44, 44, 43, 43, 44, 44, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 44, 44, 45, 45, 45, 44, 44, 44)

    // get the current time to check if the current hour is displayed correctly, also needed for the tag
    val format = DateTimeFormatter.ofPattern("HH:mm")
    val current_time = LocalDateTime.now().format(format)

    if (historical_page) {
        //go to historical page
        onNodeWithText("Historical").performClick()
        onNodeWithTag("Historical_page").assertIsDisplayed()
    }

    setMetricUnits()

    setLocation(city, countryCode)

    // wait until the hourly widget is displayed
    waitUntil(timeoutMillis = 5000) {
        onAllNodesWithTag("hourlyWidget").fetchSemanticsNodes().isNotEmpty()
    }

    var beforeNow = true
    // check if the hourly temperatures and icons are correct
    for (i in 0..47) {
        val addNull = if (i < 10) "0" else ""
        if ("$addNull$i" == current_time.substring(0, 2)) {
            beforeNow = false
        }

        // check if the temperature and icon are correct in that hour
        onNodeWithTag("hourlyWidget_hourly_temperature_$i").assertTextEquals("${temps[i % 24]}°C")
        onNodeWithTag("hourlyWidget_hourly_icon_$i").assertTextEquals(icons[i % 24])
        if (beforeNow) {
            onNodeWithTag("hourlyWidget_hourly_precipitation_$i").assertTextEquals("${perciptation[i % 24]} mm")
        } else {
            onNodeWithTag("hourlyWidget_hourly_precipitation_$i").assertTextEquals("${precipitation_probability[i % 24]}%")
        }
    }

    // also test with imperial units
    setImperialUnits()
    beforeNow = true
    waitUntil(timeoutMillis = 5000) {
        onAllNodesWithTag("hourlyWidget").fetchSemanticsNodes().isNotEmpty()
    }
    for (i in 0..47) {
        val addNull = if (i < 10) "0" else ""
        if ("$addNull$i" == current_time.substring(0, 2)) {
            beforeNow = false
        }

        onNodeWithTag("hourlyWidget_hourly_temperature_$i").assertTextEquals("${f_temps[i % 24]}°F")
        onNodeWithTag("hourlyWidget_hourly_icon_$i").assertTextEquals(icons[i % 24])
        if (beforeNow) {
            onNodeWithTag("hourlyWidget_hourly_precipitation_$i").assertTextEquals("${perciptation[i % 24]} in")
        } else {
            onNodeWithTag("hourlyWidget_hourly_precipitation_$i").assertTextEquals("${precipitation_probability[i % 24]}%")
        }
    }
}

/**
 * Tests the ten days widget with the given data.
 * It sets the location to Karlskrona and checks if the daily temperatures and icons are correct.
 * @param setLocation a function to set the location for a given city
 * @param setImperialUnits a function to set the units to imperial
 * @param historical_page if true, the test is for the historical page, otherwise for the home page
 */
fun ComposeTestRule.testTenDaysWidget(setLocation: (String, String) -> Unit, setImperialUnits: () -> Unit, setMetricUnits: () -> Unit, historical_page: Boolean) {

    val city = "Berlin"
    val countryCode = "DE"
    val precipitation = listOf("1", "0", "5", "0", "0", "2", "3", "2", "0", "0", "0", "0", "0", "15", "0", "0", "0", "2")
    val weatherCodes = listOf("3", "51", "51", "51", "51", "51", "3", "85", "85", "3", "51", "1", "2", "85")
    val icons = weatherCodes.map { weatherCodesMap[it].toString() }


    val datesRange =  getDateRange()
    val dates = datesRange.map { localDateToString(it, false) }
    // in Celsius
    val temps = listOf(5, 7, 4, 6, 2, 1, -2, 0, 4, 4, 3, 2, 1, 1)
    // in Fahrenheit
    val f_temps = listOf(42, 45, 40, 44, 36, 34, 28, 33, 39, 39, 37, 35, 34, 33)

    if (historical_page) {
        //go to historical page
        onNodeWithText("Historical").performClick()
        onNodeWithTag("Historical_page").assertIsDisplayed()
    }

    setLocation(city, countryCode)

    setMetricUnits()

    // wait until the hourly widget is displayed
    waitUntil(timeoutMillis = 5000) {
        onAllNodesWithTag("dayWidget").fetchSemanticsNodes().isNotEmpty()
    }
    // check if the daily temperatures and icons are correct
    for (i in 0..13) {
        onNodeWithTag("dayWidget_temperature_${dates[i]}").assertTextEquals("${temps[i]}°C")
        onNodeWithTag("dayWidget_icon_${dates[i]}").assertTextEquals(icons[i])
        onNodeWithTag("dayWidget_precipitation_${dates[i]}").assertTextEquals("${precipitation[i]} mm")
    }

    // also test with imperial units
    setImperialUnits()
    waitUntil(timeoutMillis = 5000) {
        onAllNodesWithTag("dayWidget").fetchSemanticsNodes().isNotEmpty()
    }
    for (i in 0..13) {
        onNodeWithTag("dayWidget_temperature_${dates[i]}").assertTextEquals("${f_temps[i]}°F")
        onNodeWithTag("dayWidget_icon_${dates[i]}").assertTextEquals(icons[i])
        onNodeWithTag("dayWidget_precipitation_${dates[i]}").assertTextEquals("${precipitation[i]} in")
    }
}

val weatherCodesMap = mapOf<String, String>(
    "0" to "\u2600", // Clear sky ☀
    "1" to "\uD83C\uDF24", // Mainly clear 🌤
    "2" to "\u26C5", // Partly cloudy ⛅
    "3" to "\u2601", // Overcast ☁
    "45" to "\uD83C\uDF2B", // Fog 🌫
    "48" to "\uD83C\uDF2B", // Depositing rime fog 🌫
    "51" to "\uD83C\uDF27", // Drizzle: Light 🌧
    "53" to "\uD83C\uDF27", // Drizzle: Moderate 🌧
    "55" to "\uD83C\uDF27", // Drizzle: Dense 🌧
    "56" to "\u2744", // Freezing Drizzle: Light ❄
    "57" to "\u2744", // Freezing Drizzle: Dense ❄
    "61" to "\uD83C\uDF26", // Rain: Slight 🌦
    "63" to "\uD83C\uDF26", // Rain: Moderate 🌦
    "65" to "\uD83C\uDF26", // Rain: Heavy 🌦
    "66" to "\u2744", // Freezing Rain: Light ❄
    "67" to "\u2744", // Freezing Rain: Heavy ❄
    "71" to "\uD83C\uDF28", // Snow fall: Slight 🌨
    "73" to "\uD83C\uDF28", // Snow fall: Moderate 🌨
    "75" to "\uD83C\uDF28", // Snow fall: Heavy 🌨
    "77" to "\u2744", // Snow grains ❄
    "80" to "\uD83C\uDF26", // Rain showers: Slight 🌦
    "81" to "\uD83C\uDF26", // Rain showers: Moderate 🌦
    "82" to "\uD83C\uDF26", // Rain showers: Violent 🌦
    "85" to "\uD83C\uDF28", // Snow showers: Slight 🌨
    "86" to "\uD83C\uDF28", // Snow showers: Heavy 🌨
    "95" to "\u26A1", // Thunderstorm: Slight or moderate ⚡
    "96" to "\u26A1", // Thunderstorm with slight hail ⚡
    "99" to "\u26A1" // Thunderstorm with heavy hail ⚡
)
