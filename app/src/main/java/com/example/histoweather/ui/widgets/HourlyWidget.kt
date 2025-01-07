package com.example.histoweather.ui.widgets


import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.histoweather.GlobalVariables
import com.example.histoweather.R
import com.example.histoweather.api.OpenMeteoParameters
import com.example.histoweather.api.weather.Hour
import com.example.histoweather.api.weather.PlaceWeatherData
import com.example.histoweather.api.weather.WeatherClient
import com.example.histoweather.ui.localTimeToString
import com.example.histoweather.ui.indexOfDateTimeInHours
import com.example.histoweather.ui.isAfterSelectedDate
import com.example.histoweather.ui.isCurrentHour
import com.example.histoweather.ui.precipitationToString
import com.example.histoweather.ui.theme.Colors
import com.example.histoweather.ui.widthRatioToPx
import java.time.LocalDateTime
import kotlin.math.roundToInt

/**
 * Hourly Widget
 */
class HourlyWidget : Widget {
    val hourlyRange = Pair(0L, 1L) // in days. 0, 0 is a full day

    /**
     * Constructor adds the required parameters to the OpenMeteoClient
     */
    constructor(client: WeatherClient) : super(client) {
        client.addHourlyRequirements(
            listOf(
                "temperature_2m",
                "weather_code",
                "precipitation_probability",
                "precipitation",
            )
        )

        client.addHourlyRange(hourlyRange)
    }

    /**
     * Widget for a single hour in the scrollable widget
     */
    @Composable
    fun HourlyWidgetSmall(hour: Hour, color: Color, index: Int, isMetric: Boolean) {
        TemplateWidget(
            width = "15%",
            height = "20%",
            position = "left",
            padding = "0,5,5,0",
            color = color,
            outline = isCurrentHour(hour.time),
            outlineColor = Colors.subWidgetOutline,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,

                modifier = Modifier.fillMaxSize()

            ) {
                val temperature = hour.properties["temperature_2m"]?.toFloat()?.roundToInt()
                val symbol = if (isMetric) "C" else "F"
                val icon =
                    OpenMeteoParameters.weatherCodesMap[hour.properties["weather_code"]] ?: "?"
                val time = localTimeToString(hour.time)
                val precipitation = if (hour.time > LocalDateTime.now().minusHours(1)) {
                    hour.properties["precipitation_probability"] + "%"
                } else {
                    precipitationToString(
                        hour.properties["precipitation"]?.toFloat(),
                        isMetric
                    )
                }
                Text(time, textAlign = TextAlign.Center, color = Colors.subWidgetForeground)
                Text(
                    text = icon,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Colors.subWidgetForeground,
                    modifier = Modifier.testTag("hourlyWidget_hourly_icon_$index")
                )
                Text(
                    text = precipitation,
                    textAlign = TextAlign.Center,
                    color = Colors.precipitation,
                    modifier = Modifier.testTag("hourlyWidget_hourly_precipitation_$index")
                )
                Text(
                    "$temperature°$symbol",
                    textAlign = TextAlign.Center,
                    color = Colors.subWidgetForeground,
                    modifier = Modifier.testTag("hourlyWidget_hourly_temperature_$index")
                )
            }
        }
    }

    /**
     * Draw the Hourly Widget. It is scrollable and displays the hourly forecast for the next 24 hours.
     * @param results a list of PlaceWeatherData objects resulting from the earlier given OpenMeteoClient
     */
    @Composable
    override fun Draw(results: List<PlaceWeatherData>, isMetric: Boolean) {
        TemplateWidget(
            width = "max",
            height = "27%",
            position = "center",
            padding = "20,10,20,10",
            color = Colors.widgetBackground
        ) {
            val result = results[0]
            val hoursList = result.getRelativeHourlyData(hourlyRange)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 25.dp, top = 10.dp, end = 25.dp, bottom = 25.dp)
                    .testTag("hourlyWidget")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Clock Icon",
                        modifier = Modifier
                            .size(28.dp)
                            .padding(top = 10.dp),
                        tint = Colors.widgetForeground,
                    )
                    Text(
                        stringResource(R.string.hourly_widget),
                        modifier = Modifier.padding(top = 10.dp),
                        color = Colors.widgetForeground,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))


                // Find the current hour index to display first this hour in the scroll

                val currentTime = LocalDateTime.now()
                var startIndex: Int = 0

                // calculate the start index based on the current hour
                try {
                    startIndex = indexOfDateTimeInHours(hoursList, currentTime)
                } catch (e: NoSuchElementException) {
                    Log.e("ScrollError", "Current hour not found in hourList: ${e.message}")
                }

                val scrollState = rememberScrollState()

                val dateSelected = GlobalVariables.primaryDate.value
                val widgetWidth = widthRatioToPx(
                    0.15f,
                    configuration = LocalConfiguration.current,
                    density = LocalDensity.current
                )

                // when the date changes or the start index changes, scroll to the new position accordingly
                LaunchedEffect(dateSelected, startIndex) {
                    // 0 if the exact hour isn't contained in the list
                    scrollState.scrollTo((widgetWidth * startIndex).toInt())
                }

                Row(
                    modifier = Modifier.horizontalScroll(scrollState)
                ) {
                    hoursList.forEachIndexed { i, hour ->
                        val color = if (isAfterSelectedDate(hour.time, result.queryDate))
                            Colors.subWidgetBackgroundVariant
                        else
                            Colors.subWidgetBackground
                        HourlyWidgetSmall(hour, color, i, isMetric)
                    }
                }
            }
        }
    }
}