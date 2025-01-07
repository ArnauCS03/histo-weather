package com.example.histoweather.ui.widgets

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.histoweather.api.weather.PlaceWeatherData
import com.example.histoweather.api.weather.WeatherClient
import com.example.histoweather.ui.heightRatioToDp
import com.example.histoweather.ui.widthRatioToDp

abstract class Widget (client: WeatherClient) {
    @Composable
    abstract fun Draw(results: List<PlaceWeatherData>, isMetric: Boolean)
}

// Template Widget
// All the parameters are optional, if not indicated the default values are the ones below
/**
 * Template Widget
 * A customizable widget with parameters for width, height, padding, color, position, and roundness
 * All parameters are optional, if not indicated the default values are the ones below
 * @param width width of the widget, "max" for fillMaxWidth or percentage string like "50%"
 * @param height height of the widget, "max" or percentage string like "50%"
 * @param padding single value for all sides the same or comma-separated  start,top,end,bottom
 * @param color background color of the widget
 * @param position horizontal position: "left", "center", or "right"
 * @param round true if the widget should have rounded corners
 * @param content for content inside the widget like Text or Column
 */
@Composable
fun TemplateWidget(
    width: String = "max",
    height: String = "max",
    padding: String = "0",
    color: Color = MaterialTheme.colorScheme.primary,
    position: String = "center",
    round: Boolean = true,
    outline: Boolean = false,
    outlineColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit = {}
) {
    // Get screen dimensions
    val configuration = LocalConfiguration.current
    // Get density for converting dp to px
    val density = LocalDensity.current

    // handle the input and set the width of the widget accordingly
    val widthModifier = when {
        width == "max" -> Modifier.fillMaxWidth()
        width.endsWith("%") -> {
            val ratio = (width.dropLast(1).toFloatOrNull() ?: 100f) / 100f
            try {
                Modifier.width(widthRatioToDp(ratio, configuration, density))
            } catch (_: IllegalArgumentException) {
                Log.e(
                    "WidthCalculationError",
                    "Width ratio is out of bounds. Please use values between 0% and 100%"
                )
                Modifier.width(0.dp)
            }
        }

        else -> Modifier.width(width.toIntOrNull()?.dp ?: 0.dp)
    }

    // handle the input and set the height of the widget accordingly
    val heightModifier = when {
        height == "max" -> Modifier.fillMaxHeight()
        height.endsWith("%") -> {
            val ratio = (height.dropLast(1).toFloatOrNull() ?: 100f) / 100f
            try {
                Modifier.height(heightRatioToDp(ratio, configuration, density))
            } catch (_: IllegalArgumentException) {
                Log.e(
                    "HeightCalculationError",
                    "Height ratio is out of bounds. Please use values between 0% and 100%"
                )
                Modifier.height(0.dp)
            }
        }

        else -> Modifier.height(height.toIntOrNull()?.dp ?: 0.dp)
    }

    val paddingValues = padding.split(",").map { it.trim().toIntOrNull() ?: 0 }
    val resolvedPadding = if (paddingValues.size == 1) {
        PaddingValues(paddingValues[0].dp) // single value applies to all sides
    } else {
        PaddingValues(
            start = paddingValues.getOrNull(0)?.dp ?: 0.dp,
            top = paddingValues.getOrNull(1)?.dp ?: 0.dp,
            end = paddingValues.getOrNull(2)?.dp ?: 0.dp,
            bottom = paddingValues.getOrNull(3)?.dp ?: 0.dp
        )
    }

    val alignment = when (position.lowercase()) {
        "left" -> Alignment.CenterStart
        "right" -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    // Shape of the widget
    val shape = if (round) RoundedCornerShape(16.dp) else RoundedCornerShape(0.dp)

    // Outline of the widget
    val outlineModifier = if (outline) {
        Modifier.border(
            width = 3.dp,
            color = outlineColor,
            shape = shape,
        )
    } else {
        Modifier
    }

    // Box wrapping the template  (uses all the custom parameters)
    Box(
        modifier = Modifier
            .then(widthModifier)
            .then(heightModifier)
            .padding(resolvedPadding)
            .then(outlineModifier)
            .background(color = color, shape = shape),
        contentAlignment = alignment
    ) {
        content()   // function that will be called inside the widget, like Text or Column
    }
}
