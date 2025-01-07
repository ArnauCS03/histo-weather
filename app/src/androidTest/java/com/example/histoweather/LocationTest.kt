package com.example.histoweather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationTest {
    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        GlobalVariables.testing = true
    }

    @Test
    fun testLocationPageFavourites() {
        // add a location to the favorites
        addFavorite("Berlin", "DE")
        addFavorite("Hamburg", "DE")
        addFavorite("Stuttgart", "DE")
        addFavorite("Cologne", "DE")

        // check if the favorites are displayed
        composeTestRule.onNodeWithTag("Home_locationButton").performClick()
        testLocation("location_favorite_Berlin_DE", "Berlin")
        testLocation("location_favorite_Hamburg_DE", "Hamburg")
        testLocation("location_favorite_Stuttgart_DE", "Stuttgart")
        testLocation("location_favorite_Cologne_DE", "Cologne")

        deleteLocationFav("location_listElement_Berlin_DE", "Berlin")
        deleteLocationFav("location_listElement_Hamburg_DE", "Hamburg")
        deleteLocationFav("location_listElement_Stuttgart_DE", "Stuttgart")
        deleteLocationFav("location_listElement_Cologne_DE", "Cologne")

        // go back to home page
        composeTestRule.onNodeWithText("Home").performClick()
    }

    @Test
    fun testLocationPageHistory(){
        // add a location to the history
        setLocation("Berlin", "DE")
        setLocation("Hamburg", "DE")
        setLocation("Stuttgart", "DE")
        setLocation("Cologne", "DE")

        // check if the history is displayed (was not working with onNodeWithTag)
        composeTestRule.onNodeWithTag("Home_locationButton").performClick()
        composeTestRule.onNodeWithTag("search_bar_input").performClick()
        testLocation("location_history_Berlin_DE", "Berlin")
        testLocation("location_history_Hamburg_DE", "Hamburg")
        testLocation("location_history_Stuttgart_DE", "Stuttgart")
        testLocation("location_history_Cologne_DE", "Cologne")

        // go back to home page
        composeTestRule.onNodeWithText("Home").performClick()
    }

    /**
     * Tests the current location button.
     */
    @Test
    fun testCurrentLocation(){
        // click on the current location button
        composeTestRule.onNodeWithTag("Home_locationButton").performClick()
        composeTestRule.onNodeWithTag("location_currentLocation").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()

        // check if the weather for the current location is displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("currentWidget_current_temperature").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("currentWidget_current_temperature").assertIsDisplayed()
    }

    /**
     * Tests with coarse location permission.
     */
    @Test
    fun testWithCoarseLocation() {
        val permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionRule.apply {
            testCurrentLocation()
        }
    }

    /**
     * Sets the location to the given city. Clicks on the location button, types the city in the search bar and selects the city from the list and gets back to the home page.
     */
    private fun setLocation(city: String, countryCode: String) {
        composeTestRule.onNodeWithTag("Home_locationButton").performClick()
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput(city)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("location_listElement_${city}_$countryCode")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("location_listElement_${city}_$countryCode").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()
    }

    /**
     * Adds a location to the favorites. Clicks on the location button, types the city in the search bar and selects the city from the list and adds it to the favorites.
     */
    private fun addFavorite(city: String, countryCode: String) {
        composeTestRule.onNodeWithTag("Home_locationButton").performClick()
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput(city)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("location_listElement_${city}_$countryCode")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithContentDescription("location_noFavorite_${city}_$countryCode").performClick()
        composeTestRule.onNodeWithTag("location_listElement_${city}_$countryCode").performClick()
        composeTestRule.onNodeWithTag("Home_page").assertIsDisplayed()
    }

    /**
     * Tests the location search bar. Types the city in the search bar and checks if the city is displayed in the list. Then clears the search bar.
     */
    private fun testLocation(contentDescripion: String, city: String) {
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput(city)
        composeTestRule.onNodeWithContentDescription(contentDescripion).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("search_bar_clear").performClick()
    }

    /**
     * Deletes a location from the favorites. Clicks on the location button, types the city in the search bar and selects the city from the list and deletes it from the favorites.
     */
    private fun deleteLocationFav(contentDescription: String, city: String) {
        composeTestRule.onNodeWithTag("location_backButton").performClick()
        composeTestRule.onNodeWithTag("Home_locationButton").performClick()
        composeTestRule.onNodeWithTag(contentDescription).performTouchInput { longClick(durationMillis = 1000, position = centerLeft) }
        composeTestRule.onNodeWithText("Yes").performClick()
    }
}