package com.example.histoweather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.histoweather.GlobalVariables
import com.example.histoweather.MainActivity
import com.example.histoweather.ui.theme.ThemeMode
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest {

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        GlobalVariables.testing = true
    }

    @Test
    fun testThemeSwitcher() {
        // go to settings page
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_page").assertIsDisplayed()

        // light
        composeTestRule.onNodeWithTag("settings_theme_switcher_row").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Light").performClick()
        assertEquals(ThemeMode.Light, GlobalVariables.themeMode.value)

        // dark
        composeTestRule.onNodeWithTag("settings_theme_switcher_row").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Dark").performClick()
        assertEquals(ThemeMode.Dark, GlobalVariables.themeMode.value)

        // system
        composeTestRule.onNodeWithTag("settings_theme_switcher_row").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("System").performClick()
        assertEquals(ThemeMode.System, GlobalVariables.themeMode.value)
    }

    @Test
    fun testUnitSwitcher() {
        // go to settings page
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithTag("settings_page").assertIsDisplayed()

        // imperial
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Imperial").performClick()
        composeTestRule.onNodeWithText("Imperial: °F, inch").assertIsDisplayed()

        // metric
        composeTestRule.onNodeWithTag("settings_unit_switcher_row").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Metric").performClick()
        composeTestRule.onNodeWithText("Metric: °C, mm").assertIsDisplayed()
    }


}