package com.example.histoweather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HistoricalTest {

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
        //go to historical page
        composeTestRule.onNodeWithText("Historical").performClick()
        composeTestRule.onNodeWithTag("Historical_page").assertIsDisplayed()

        composeTestRule.clickAndVerifyWithTags("Historical_dateButton", "date_picker_dialog")
    }

    @Test
    fun testCompareButton_navigatesToComparePage_whenClicked() {
        //go to historical page
        composeTestRule.onNodeWithText("Historical").performClick()
        composeTestRule.onNodeWithTag("Historical_page").assertIsDisplayed()

        composeTestRule.clickAndVerifyWithTags("Historical_compareButton", "compare_page")
    }

    @Test
    fun testLocationButton_navigatesToLocationPage_whenClicked() {
        //go to historical page
        composeTestRule.onNodeWithText("Historical").performClick()
        composeTestRule.onNodeWithTag("Historical_page").assertIsDisplayed()

        composeTestRule.clickAndVerifyWithTags("Historical_locationButton", "location_page")
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
        }
    }

    @Test
    fun testHourlyWidget(){
        composeTestRule.testHourlyWidget(setLocation = this::setLocation, setImperialUnits = this::setImperialUnits, setMetricUnits = this::setMetricUnits, true)
    }

    @Test
    fun testTenDaysWidget(){
        composeTestRule.testTenDaysWidget(setLocation = this::setLocation, setImperialUnits = this::setImperialUnits, setMetricUnits = this::setMetricUnits, true)
    }

    /**
     * Sets the location to the given city. Clicks on the location button, types the city in the search bar and selects the city from the list and gets back to the historical page.
     */
    private fun setLocation(city : String, countryCode : String){
        composeTestRule.onNodeWithTag("Historical_locationButton").performClick()
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput(city)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("location_listElement_${city}_$countryCode").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("location_listElement_${city}_$countryCode").performClick()
        composeTestRule.onNodeWithTag("Historical_page").assertIsDisplayed()
    }

    /**
     * Sets the imperial units in the settings. Clicks on the settings button, clicks on the unit switcher and gets back to the Historical page.
     */
    private fun setImperialUnits() {
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").performClick()
        composeTestRule.onNodeWithText("Imperial").performClick()
        composeTestRule.onNodeWithText("Historical").performClick()
    }

    /**
     * Sets the metric units in the settings. Clicks on the settings button, clicks on the unit switcher and gets back to the Home page.
     */
    private fun setMetricUnits() {
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").performClick()
        composeTestRule.onNodeWithText("Metric").performClick()
        composeTestRule.onNodeWithText("Historical").performClick()
    }
}