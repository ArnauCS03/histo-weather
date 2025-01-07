package com.example.histoweather.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.histoweather.ui.theme.ThemeMode
import com.example.histoweather.R


/**
 * Settings page currently with Theme and Unit Switcher
 * @param onThemeChange Lambda function to handle theme change.
 * @param themeMode current theme mode (Light, Dark, System)
 */
@Composable
fun Settings(
    onThemeChange: (ThemeMode) -> Unit,
    themeMode: ThemeMode,
    isMetric: Boolean,
    onMetricChange: (Boolean) -> Unit
    ) {
    // Main Column
    Column (modifier = Modifier.testTag("settings_page") ) {
        // default padding
        val padding = 8.dp

        // Headline
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(padding)
        )

        // Theme Switcher
        OutlinedCard(modifier = Modifier.padding(padding)) {
            ThemeSwitcher(onThemeChange = onThemeChange, themeMode = themeMode)
        }

        // Unit Switcher
        OutlinedCard(modifier = Modifier.padding(padding)) {
            UnitSwitcher(
                padding,
                isMetric = isMetric,
                onToggleUnit = { onMetricChange(!isMetric)}
            )
        }
    }
}

/**
 * Unit Switcher
 * @param padding default padding
 */
@Composable
fun UnitSwitcher(padding: Dp, isMetric: Boolean, onToggleUnit: (Boolean) -> Unit) {
    // true if dropdown is shown (metric, imperial)
    var expanded by remember { mutableStateOf(false) }
    // available options
    val options = listOf(stringResource(R.string.s_metric), stringResource(R.string.s_imperial))

    // main row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.clickable { expanded = true }.testTag("settings_unit_switcher_row")
    ) {
        Spacer(modifier = Modifier.width(padding))
        // Column for left side text and description
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(id = R.string.s_units),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (isMetric) stringResource(R.string.s_metric_full)
                else stringResource(R.string.s_imperial_full),
                style = MaterialTheme.typography.labelMedium
            )
        }
        //spacer for space between text and switch
        Spacer(modifier = Modifier.weight(1f))

        // Column for right side iconButton and dropdownMenu
        Column {
            IconButton(onClick = { expanded = true }, modifier = Modifier.padding(8.dp)) {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Change Theme"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            // first one is metric
                            if ((unit == options[0]) != isMetric) { // Only toggle if the unit is different
                                onToggleUnit(unit == "Metric")
                            }
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


/**
 * Theme Switcher
 * @param onThemeChange Lambda function to handle theme change.
 * @param themeMode current theme mode
 */
@Composable
fun ThemeSwitcher(onThemeChange: (ThemeMode) -> Unit, themeMode: ThemeMode) {
    // true if dropdown is shown (light, dark, system)
    var expanded by remember { mutableStateOf(false) }
    // available options
    val options = listOf(ThemeMode.Light, ThemeMode.Dark, ThemeMode.System)

    // main row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.clickable { expanded = true }.testTag("settings_theme_switcher_row")
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        // Column for left side text and description
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                // title
                text = stringResource(id = R.string.s_theme),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                // description
                text = themeMode.name,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        // Column for right side iconButton and dropdownMenu
        Column {
            IconButton(onClick = { expanded = true }, modifier = Modifier.padding(8.dp)) {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Change Theme"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(theme.name) },
                        onClick = {
                            onThemeChange(theme)
                            expanded = false
                        },
                        modifier = Modifier.testTag("settings_theme_switcher_dropdown_"+theme.name)
                    )
                }
            }
        }
    }
}
