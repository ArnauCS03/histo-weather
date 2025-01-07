package com.example.histoweather.ui.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
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
import com.example.histoweather.ui.heightRatioToPx
import com.example.histoweather.ui.indexOfDateTimeInHours
import com.example.histoweather.ui.isAfterSelectedDate
import com.example.histoweather.ui.isCurrentHour
import com.example.histoweather.ui.localTimeToString
import com.example.histoweather.ui.precipitationToString
import com.example.histoweather.ui.theme.Colors
import kotlin.math.roundToInt

/**
 * Hourly Comparison Widget
 */
class HourlyComparisonWidget: Widget {
    val hourlyRange = Pair(0L, 1L) // in days. 0, 0 is a full day

    /**
     * Constructor adds the required parameters to the OpenMeteoClient
     */
    constructor(client: WeatherClient): super(client) {
        client.addHourlyRequirements(
            listOf(
                "temperature_2m",
                "weather_code",
                "precipitation",
            )
        )

        client.addHourlyRange(hourlyRange)
    }

    /**
     * Widget for a single hour on the left side of the comparison
     */
    @Composable
    fun LeftHourWidget(hour: Hour, color: Color, index: Int, isMetric: Boolean) {
        TemplateWidget(
            width = "30%",
            height = "5%",
            position = "center",
            padding = "0",
            color = color,
            outline = isCurrentHour(hour.time),
            outlineColor = Colors.subWidgetOutline
        ) {
            val temperature = hour.properties["temperature_2m"]?.toFloat()?.roundToInt()
            val symbol = if (isMetric) "C" else "F"
            val icon =
                OpenMeteoParameters.weatherCodesMap[hour.properties["weather_code"]] ?: "?"
            val precipitation = precipitationToString(
                hour.properties["precipitation"]?.toFloat(),
                isMetric
            )

            Row {
                Text(
                    text = icon,
                    modifier = Modifier.testTag("hourlyComparison_left_icon_$index"),
                    color = Colors.subWidgetForeground,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = "$temperature°$symbol",
                    modifier = Modifier.testTag("hourlyComparison_left_temperature_$index"),
                    color = Colors.subWidgetForeground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = precipitation,
                    color = Colors.precipitation,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("hourlyComparison_left_precipitation_$index")
                )
            }
        }
    }

    /**
     * Widget for a single hour on the right side of the comparison
     */
    @Composable
    fun RightHourWidget(hour: Hour, color: Color, index: Int, isMetric: Boolean) {
        TemplateWidget(
            width = "35%",
            height = "5%",
            position = "center",
            padding = "0",
            color = color,
            outline = isCurrentHour(hour.time),
            outlineColor = Colors.subWidgetOutline
        ) {
            val temperature = hour.properties["temperature_2m"]?.toFloat()?.roundToInt()
            val symbol = if (isMetric) "C" else "F"
            val icon =
                OpenMeteoParameters.weatherCodesMap[hour.properties["weather_code"]] ?: "?"
            val precipitation = precipitationToString(
                hour.properties["precipitation"]?.toFloat(),
                isMetric
            )

            Row {
                Text(
                    text = precipitation,
                    modifier = Modifier.testTag("hourlyComparison_right_precipitation_$index"),
                    color = Colors.precipitation,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = "$temperature°$symbol",
                    color = Colors.subWidgetForeground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("hourlyComparison_right_temperature_$index")
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = icon,
                    color = Colors.subWidgetForeground,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    modifier = Modifier.testTag("hourlyComparison_right_icon_$index")
                )
            }
        }
    }

    /**
     * Widget for a single hour comparison
     * @param hourA Hour object
     * @param hourB Hour object
     */
    @Composable
    fun HourlyComparisonWidgetSmall(hourA: Hour, hourB: Hour, color: Color, index: Int, isMetric: Boolean) {
        if (hourA.time.hour != hourB.time.hour) {
            Text("hours not identical!")
            return
        }

        TemplateWidget(
            width = "max",
            height = "5%",
            position = "center",
            padding = "0,5,25,0",
            color = Colors.widgetBackground,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,

                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,

                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    LeftHourWidget(hourA, color, index, isMetric)
                    Text(localTimeToString(hourA.time), color = Colors.widgetForeground)
                    RightHourWidget(hourB, color, index, isMetric)
                }
            }

        }
    }

    /**
     * Draw the Hourly Comparison Widget
     * @param resultA a PlaceWeatherData object resulting from the earlier given OpenMeteoClient using the first location
     * @param resultB a PlaceWeatherData object resulting from the earlier given OpenMeteoClient using the second location
     */
    @Composable
    override fun Draw(results: List<PlaceWeatherData>, isMetric: Boolean) {
        TemplateWidget(
            width = "max",
            height = "30%",
            position = "left",
            padding = "20,10,20,10",
            color = Colors.widgetBackground
        ) {
            val resultA = results[0]
            val resultB = results[1]
            val hoursListA = resultA.getRelativeHourlyData(hourlyRange)
            val hoursListB = resultB.getRelativeHourlyData(hourlyRange)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 25.dp, top = 10.dp)
                    .testTag("hourlyComparisonWidget")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Clock Icon",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 5.dp),
                        tint = Colors.widgetForeground,
                    )
                    Text(stringResource(R.string.hourly_compare_widget), color = Colors.widgetForeground)
                }


                // Find the current hour index to display first this hour in the scroll

                val currentTime = java.time.LocalDateTime.now()
                var startIndex: Int = 0

                try {
                    // using the function that returns the index of the current hour in the list matching the current time (hour)
                    startIndex = indexOfDateTimeInHours(resultA.hourList, currentTime)
                } catch (e: NoSuchElementException) {
                    Log.e("ScrollError", "Current hour not found in hourList: ${e.message}")
                }

                val scrollState = rememberScrollState()

                val dateSelected = GlobalVariables.primaryDate.value
                val currentDate = java.time.LocalDate.now()
                val widgetHeight = heightRatioToPx(
                    0.05f,
                    configuration = LocalConfiguration.current,
                    density = LocalDensity.current
                )

                LaunchedEffect(dateSelected, startIndex) {
                    if (dateSelected == currentDate) {   // check if we have a current hour
                        scrollState.scrollTo((startIndex * widgetHeight).toInt())
                    } else {
                        // no need to scroll, will display 00:00h the first value
                        scrollState.scrollTo(0)
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize()
                        .padding(bottom = 10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        hoursListA.zip(hoursListB).forEachIndexed { index, (hourA, hourB) ->
                            val color = if (isAfterSelectedDate(hourA.time, resultA.queryDate)) {
                                Colors.subWidgetBackgroundVariant
                            } else {
                                Colors.subWidgetBackground
                            }
                            HourlyComparisonWidgetSmall(hourA, hourB, color, index, isMetric)
                        }
                    }
                }
            }
        }
    }
}