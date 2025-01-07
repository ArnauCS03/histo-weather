package com.example.histoweather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.rule.GrantPermissionRule
import com.example.histoweather.ui.localDateToString
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CompareTest {
    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        GlobalVariables.testing = true
    }

    @Test
    fun testDateButtons_displaysDatePickerDialog_whenClicked() {
        //go to compare page
        composeTestRule.onNodeWithText("Compare").performClick()
        composeTestRule.onNodeWithTag("compare_page").assertIsDisplayed()

        composeTestRule.onNodeWithTag("compare_date_button_0").isDisplayed()
        composeTestRule.clickAndVerifyWithTags("compare_date_button_0", "date_picker_dialog")
        Espresso.pressBack()
        composeTestRule.clickAndVerifyWithTags("compare_date_button_1", "date_picker_dialog")
    }

    @Test
    fun testLocationButtons_navigatesToLocationPage_whenClicked() {
        //go to compare page
        composeTestRule.onNodeWithText("Compare").performClick()
        composeTestRule.onNodeWithTag("compare_page").assertIsDisplayed()

        // test location buttons with back gesture
        composeTestRule.clickAndVerifyWithTags("compare_location_button_0", "location_page")
        Espresso.pressBack()
        composeTestRule.clickAndVerifyWithTags("compare_location_button_1", "location_page")
        Espresso.pressBack()
        composeTestRule.onNodeWithTag("compare_page").assertIsDisplayed()

        // test location buttons with back button
        composeTestRule.clickAndVerifyWithTags("compare_location_button_0", "location_page")
        composeTestRule.clickAndVerifyWithTags("location_backButton", "compare_page")
        composeTestRule.clickAndVerifyWithTags("compare_location_button_1", "location_page")
        composeTestRule.clickAndVerifyWithTags("location_backButton", "compare_page")
    }

    /**
     * Makes the tests with coarse location permission, because the tests were made with fine location permission before.
     */
    @Test
    fun testWithCoarseLocation() {
        val permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionRule.apply {
            testDateButtons_displaysDatePickerDialog_whenClicked()
            testLocationButtons_navigatesToLocationPage_whenClicked()
        }
    }

    /**
     * Tests the hourly comparison widget. Sets the location to Karlskrona and Stockholm and checks if the temperatures and icons are correct.
     */
    @Test
    fun testHourlyCompareWidget() {
        val city1 = "Berlin"
        val city2 = "Stuttgart"
        val countryCode1 = "DE"
        val countryCode2 = "DE"

        val perciptation = listOf("0", "2", "0", "7", "0", "1", "1", "2", "2", "7", "0", "1", "0", "3", "5", "0", "4", "1", "0", "2", "0", "0", "0", "1")
        val weatherCodes = listOf("3", "3", "51", "51", "3", "51", "51", "3", "51", "3", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51", "51")
        val icons1 = weatherCodes.map { weatherCodesMap[it].toString() }

        // in Celsius
        val temps1 = listOf(6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)
        // in Fahrenheit
        val f_temps1 = listOf(43, 43, 44, 44, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 44, 44, 45, 45, 45, 44, 44, 44)

        // go to compare page
        composeTestRule.onNodeWithText("Compare").performClick()

        setMetricUnits()

        setLocation(city1, countryCode1, false)
        setLocation(city2, countryCode2, true)

        // wait until the hourly widget is displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("hourlyComparisonWidget").fetchSemanticsNodes().isNotEmpty()
        }
        // check if the hourly temperatures and icons are correct
        for (i in 0..47) {
            // check if the temperature and icon are correct in that hour
            composeTestRule.onNodeWithTag("hourlyComparison_left_icon_$i").assertTextEquals(icons1[i % 24])
            composeTestRule.onNodeWithTag("hourlyComparison_right_icon_$i").assertTextEquals(icons1[i % 24])
            composeTestRule.onNodeWithTag("hourlyComparison_left_temperature_$i").assertTextEquals("${temps1[i % 24]}°C")
            composeTestRule.onNodeWithTag("hourlyComparison_right_temperature_$i").assertTextEquals(
                "${temps1[i % 24]}°C")
            composeTestRule.onNodeWithTag("hourlyComparison_left_precipitation_$i").assertTextEquals("${perciptation[i % 24]} mm")
            composeTestRule.onNodeWithTag("hourlyComparison_right_precipitation_$i").assertTextEquals("${perciptation[i % 24]} mm")
        }

        // also test with imperial units
        setImperialUnits()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("hourlyComparisonWidget").fetchSemanticsNodes().isNotEmpty()
        }
        for (i in 0..47) {
            composeTestRule.onNodeWithTag("hourlyComparison_left_icon_$i").assertTextEquals(icons1[i % 24])
            composeTestRule.onNodeWithTag("hourlyComparison_right_icon_$i").assertTextEquals(icons1[i % 24])
            composeTestRule.onNodeWithTag("hourlyComparison_left_temperature_$i").assertTextEquals("${f_temps1[i % 24]}°F")
            composeTestRule.onNodeWithTag("hourlyComparison_right_temperature_$i").assertTextEquals(
                "${f_temps1[i % 24]}°F")
            composeTestRule.onNodeWithTag("hourlyComparison_left_precipitation_$i").assertTextEquals("${perciptation[i % 24]} in")
            composeTestRule.onNodeWithTag("hourlyComparison_right_precipitation_$i").assertTextEquals("${perciptation[i % 24]} in")
        }
    }

    /**
     * Tests the daily comparison widget. Sets the location to Karlskrona and Stockholm and checks if the temperatures and icons are correct.
     */
    @Test
    fun testDailyCompareWidget() {
        val city1 = "Berlin"
        val city2 = "Frankfurt"
        val countryCode1 = "DE"
        val countryCode2 = "DE"
        val precipitation = listOf("1", "0", "5", "0", "0", "2", "3", "2", "0", "0", "0", "0", "0", "15", "0", "0", "0", "2")
        val weatherCodes = listOf("3", "51", "51", "51", "51", "51", "3", "85", "85", "3", "51", "1", "2", "85")
        val icons1 = weatherCodes.map { weatherCodesMap[it].toString() }

        val icons2 = icons1

        val datesRange =  getDateRange()
        val dates = datesRange.map { localDateToString(it, false) }

        // in Celsius
        val temps1 = listOf(5, 7, 4, 6, 2, 1, -2, 0, 4, 4, 3, 2, 1, 1)
        // in Fahrenheit
        val f_temps1 = listOf(42, 45, 40, 44, 36, 34, 28, 33, 39, 39, 37, 35, 34, 33)


        // go to compare page
        composeTestRule.onNodeWithText("Compare").performClick()

        setMetricUnits()

        setLocation(city1, countryCode1, false)
        setLocation(city2, countryCode2, true)

        // wait until the hourly widget is displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("dailyComparisonWidget").fetchSemanticsNodes().isNotEmpty()
        }
        // check if the daily temperatures and icons are correct
        for (i in 0..13) {
            composeTestRule.onNodeWithTag("dailyComparison_left_icon_${dates[i]}").assertTextEquals(icons1[i])
            composeTestRule.onNodeWithTag("dailyComparison_right_icon_${dates[i]}").assertTextEquals(icons2[i])
            composeTestRule.onNodeWithTag("dailyComparison_left_precipitation_${dates[i]}").assertTextEquals("${precipitation[i]} mm")
            composeTestRule.onNodeWithTag("dailyComparison_right_precipitation_${dates[i]}").assertTextEquals("${precipitation[i]} mm")
            composeTestRule.onNodeWithTag("dailyComparison_left_temperature_${dates[i]}").assertTextEquals("${temps1[i]}°C")
            composeTestRule.onNodeWithTag("dailyComparison_right_temperature_${dates[i]}").assertTextEquals(
                "${temps1[i]}°C")
        }

        // also test with imperial units
        setImperialUnits()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("dailyComparisonWidget").fetchSemanticsNodes().isNotEmpty()
        }
        for (i in 0..13) {
            composeTestRule.onNodeWithTag("dailyComparison_left_icon_${dates[i]}").assertTextEquals(icons1[i])
            composeTestRule.onNodeWithTag("dailyComparison_right_icon_${dates[i]}").assertTextEquals(icons2[i])
            composeTestRule.onNodeWithTag("dailyComparison_left_precipitation_${dates[i]}").assertTextEquals("${precipitation[i]} in")
            composeTestRule.onNodeWithTag("dailyComparison_right_precipitation_${dates[i]}").assertTextEquals("${precipitation[i]} in")
            composeTestRule.onNodeWithTag("dailyComparison_left_temperature_${dates[i]}").assertTextEquals("${f_temps1[i]}°F")
            composeTestRule.onNodeWithTag("dailyComparison_right_temperature_${dates[i]}").assertTextEquals(
                "${f_temps1[i]}°F")
        }
    }



    /**
     * Sets the location to the given city. Clicks on the location button, types the city in the search bar and selects the city from the list and gets back to the home page.
     */
    private fun setLocation(city : String, countryCode : String, secoundary : Boolean) {
        val loc_num = if(secoundary) 1 else 0
        composeTestRule.onNodeWithTag("compare_location_button_${loc_num}").performClick()
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput(city)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("location_listElement_${city}_$countryCode").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("location_listElement_${city}_$countryCode").performClick()
        composeTestRule.onNodeWithTag("compare_page").assertIsDisplayed()
    }

    /**
     * Sets the imperial units in the settings. Clicks on the settings button, clicks on the unit switcher and gets back to the compare page.
     */
    private fun setImperialUnits() {
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").performClick()
        composeTestRule.onNodeWithText("Imperial").performClick()
        composeTestRule.onNodeWithText("Compare").performClick()
    }

    /**
     * Sets the metric units in the settings. Clicks on the settings button, clicks on the unit switcher and gets back to the compare page.
     */
    private fun setMetricUnits() {
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").performClick()
        composeTestRule.onNodeWithText("Metric").performClick()
        composeTestRule.onNodeWithText("Compare").performClick()
    }
}