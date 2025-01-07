package com.example.histoweather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.histoweather.GlobalVariables
import com.example.histoweather.MainActivity
import com.example.histoweather.clickAndVerifyWithTags
import com.example.histoweather.testHourlyWidget
import com.example.histoweather.testTenDaysWidget
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeTest {

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        GlobalVariables.testing = true
    }

    @Test
    fun testDateButton_displaysDatePickerDialog_whenClicked() {
        //go to home page if not already there
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()

        composeTestRule.clickAndVerifyWithTags("Home_dateButton", "date_picker_dialog")
    }

    @Test
    fun testCompareButton_navigatesToComparePage_whenClicked() {
        //go to home page if not already there
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()

        composeTestRule.clickAndVerifyWithTags("Home_compareButton", "compare_page")
    }

    @Test
    fun testLocationButton_navigatesToLocationPage_whenClicked() {
        //go to home page if not already there
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()

        composeTestRule.clickAndVerifyWithTags("Home_locationButton", "location_page")
    }

    /**
     * Clicks on each item in the navigation bar and verifies that the correct page is displayed.
     */
    @Test
    fun testNavBar_navigatesToCorrectPages() {
        composeTestRule.onNodeWithText("Historical").performClick()
        composeTestRule.onNodeWithTag("Historical_page").assertIsDisplayed()
        composeTestRule.onNodeWithText("Compare").performClick()
        composeTestRule.onNodeWithTag("compare_page").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_page").assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()
    }

    /**
     * Makes the tests with coarse location permission, because the tests were made with fine location permission before.
     */
    @Test
    fun testWithCoarseLocation() {
        val permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionRule.apply {
            testDateButton_displaysDatePickerDialog_whenClicked()
            testCompareButton_navigatesToComparePage_whenClicked()
            testLocationButton_navigatesToLocationPage_whenClicked()
            testNavBar_navigatesToCorrectPages()
        }
    }

    /**
     * Tests the current widget with the given data.
     * It sets the location to Karlskrona and checks if the current temperature is 7.1°C or 44.8°F and also the high and low temperature.
     */
    @Test
    fun testCurrentWidget() {
        val city = "Berlin"
        val countryCode = "DE"
        val icon = "\uD83C\uDF27"
        // in Celsius
        var current_temp = 7.1
        var high_temp = 7.4
        var low_temp = 6.0
        val apparent_temp = 7
        // in Fahrenheit
        val f_current_temp = 44.8
        val f_high_temp = 45.4
        val f_low_temp = 42.8
        val f_apparent_temp = 44

        setLocation(city, countryCode)

        setMetricUnits()

        // wait until the current widget is displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("currentWidget_current_temperature").fetchSemanticsNodes().isNotEmpty()
        }
        // check if the current temperature is correct
        composeTestRule.onNodeWithTag("currentWidget_current_temperature").assertTextEquals("$current_temp°C")
        composeTestRule.onNodeWithTag("currentWidget_high_low_temperature").assertTextEquals("High $high_temp°C | Low $low_temp°C")
        composeTestRule.onNodeWithTag("currentWidget_weather_icon").assertTextEquals(icon)
        composeTestRule.onNodeWithTag("currentWidget_apparent_temperature").assertTextEquals("Feels like $apparent_temp°C")

        // make the same test with imperial units
        setImperialUnits()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("currentWidget_current_temperature").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("currentWidget_current_temperature").assertTextEquals("$f_current_temp°F")
        composeTestRule.onNodeWithTag("currentWidget_high_low_temperature").assertTextEquals("High $f_high_temp°F | Low $f_low_temp°F")
        composeTestRule.onNodeWithTag("currentWidget_weather_icon").assertTextEquals(icon)
        composeTestRule.onNodeWithTag("currentWidget_apparent_temperature").assertTextEquals("Feels like $f_apparent_temp°F")
    }

    @Test
    fun testHourlyWidget(){
        composeTestRule.testHourlyWidget(setLocation = this::setLocation, setImperialUnits = this::setImperialUnits, setMetricUnits = this::setMetricUnits, false)
    }

    @Test
    fun testTenDaysWidget(){
        composeTestRule.testTenDaysWidget(setLocation = this::setLocation, setImperialUnits = this::setImperialUnits, setMetricUnits = this::setMetricUnits, false)
    }

    /**
     * Sets the location to the given city. Clicks on the location button, types the city in the search bar and selects the city from the list and gets back to the home page.
     */
    private fun setLocation(city : String, countryCode : String) {
        composeTestRule.onNodeWithTag("Home_locationButton").performClick()
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput(city)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("location_listElement_${city}_$countryCode").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("location_listElement_${city}_$countryCode").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()
    }

    /**
     * Sets the imperial units in the settings. Clicks on the settings button, clicks on the unit switcher and gets back to the Home page.
     */
    private fun setImperialUnits() {
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").performClick()
        composeTestRule.onNodeWithText("Imperial").performClick()
        composeTestRule.onNodeWithText("Home").performClick()
    }

    /**
     * Sets the metric units in the settings. Clicks on the settings button, clicks on the unit switcher and gets back to the Home page.
     */
    private fun setMetricUnits() {
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").performClick()
        composeTestRule.onNodeWithText("Metric").performClick()
        composeTestRule.onNodeWithText("Home").performClick()
    }
}
