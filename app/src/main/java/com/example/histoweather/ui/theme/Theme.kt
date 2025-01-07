package com.example.histoweather.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

enum class ThemeMode {
    Light, Dark, System;

    companion object {
        fun fromString(value: String?): ThemeMode? {
            return when (value) {
                "Light" -> Light
                "Dark" -> Dark
                "System" -> System
                else -> null
            }
        }
    }
}

/**
 * HistoWeather Theme
 * @param themeMode Theme mode (Light, Dark, System) default is System
 * @param dynamicColor Use dynamic color scheme (Android 12+ only) default is true
 * @param content Content
 */
@SuppressLint("ObsoleteSdkInt")
@Composable
fun HistoWeatherTheme(
    themeMode: ThemeMode = ThemeMode.System,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine the theme based on the themeMode variable
    val darkTheme = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    Colors.precipitation = if (darkTheme) Color(0xFF1F3F8C) else Color(0xFF9EB9FF)


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}