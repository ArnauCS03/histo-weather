package com.example.histoweather.ui.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import com.example.histoweather.api.OpenMeteoParameters
import com.example.histoweather.api.weather.Day
import com.example.histoweather.api.weather.PlaceWeatherData
import com.example.histoweather.api.weather.WeatherClient
import com.example.histoweather.ui.heightRatioToPx
import com.example.histoweather.ui.indexOfDateInDays
import com.example.histoweather.ui.localDateToString
import com.example.histoweather.ui.theme.Colors
import com.example.histoweather.R
import com.example.histoweather.ui.precipitationToString
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Daily Comparison Widget
 */
class DailyComparisonWidget: Widget {
    val dailyRange = Pair(-3L, 10L)

    /**
     * Constructor adds the required parameters to the OpenMeteoClient
     */
    constructor(client: WeatherClient) : super(client) {
        client.addDailyRequirements(
            listOf(
                "temperature_2m_mean",
                "weather_code",
                "precipitation_sum",
            )
        )
        client.addDailyRange(dailyRange)
    }

    /**
     * Widget for a single day on the left side of the comparison
     * @param day Day object
     */
    @Composable
    fun LeftDayWidget(day: Day, color: Color, isMetric: Boolean) {
        TemplateWidget(
            width = "35%",
            height = "8%",
            position = "center",
            padding = "0",
            color = color,
            outline = day.date == LocalDate.now(),
            outlineColor = Colors.subWidgetOutline,
        ) {
            val temperature = day.properties["temperature_2m_mean"]?.toFloat()?.roundToInt()
            val symbol = if (isMetric) "C" else "F"
            val icon =
                OpenMeteoParameters.weatherCodesMap[day.properties["weather_code"]] ?: "?"
            val date = localDateToString(day.date, false)
            val precipitation = precipitationToString(
                day.properties["precipitation_sum"]?.toFloat(),
                isMetric
            )

            Row {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = icon,
                        color = Colors.subWidgetForeground,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier.testTag("dailyComparison_left_icon_$date")
                    )
                    Text(
                        text = precipitation,
                        color = Colors.precipitation,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("dailyComparison_left_precipitation_$date")
                    )
                }
                Spacer(modifier = Modifier.size(5.dp))
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date,
                        color = Colors.subWidgetForeground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$temperature°$symbol",
                        color = Colors.subWidgetForeground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("dailyComparison_left_temperature_$date")
                    )
                }
            }
        }
    }

    /**
     * Widget for a single day on the right side of the comparison
     * @param day Day object
     */
    @Composable
    fun RightDayWidget(day: Day, color: Color, isMetric: Boolean) {
        TemplateWidget(
            width = "35%",
            height = "8%",
            position = "center",
            padding = "0",
            color = color,
            outline = day.date == LocalDate.now(),
            outlineColor = Colors.subWidgetOutline,
        ) {
            val temperature = day.properties["temperature_2m_mean"]?.toFloat()?.roundToInt()
            val symbol = if (isMetric) "C" else "F"
            val icon =
                OpenMeteoParameters.weatherCodesMap[day.properties["weather_code"]] ?: "?"
            val date = localDateToString(day.date, false)
            val precipitation = precipitationToString(
                day.properties["precipitation_sum"]?.toFloat(),
                isMetric
            )

            Row {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date,
                        color = Colors.subWidgetForeground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$temperature°$symbol",
                        color = Colors.subWidgetForeground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("dailyComparison_right_temperature_$date")
                    )
                }
                Spacer(modifier = Modifier.size(5.dp))
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = icon,
                        color = Colors.subWidgetForeground,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier.testTag("dailyComparison_right_icon_$date")
                    )
                    Text(
                        text = precipitation,
                        color = Colors.precipitation,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("dailyComparison_right_precipitation_$date")
                    )
                }
            }
        }
    }

    /**
     * Widget for a single day comparison
     * @param dayA Day object
     * @param dayB Day object
     */
    @Composable
    fun DailyComparisonWidgetSmall(dayA: Day, dayB: Day, color: Color, isMetric: Boolean) {
        TemplateWidget(
            width = "max",
            height = "8%",
            position = "left",
            padding = "0,5,25,0",
            color = Colors.widgetBackground,
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,

                modifier = Modifier
                    .fillMaxSize()
            ) {
                LeftDayWidget(dayA, color, isMetric)
                RightDayWidget(dayB, color, isMetric)
            }

        }
    }

    /**
     * Draw the Daily Comparison Widget
     * @param resultA a PlaceWeatherData object resulting from the earlier given OpenMeteoClient using the first location
     * @param resultB a PlaceWeatherData object resulting from the earlier given OpenMeteoClient using the second location
     */
    @Composable
    override fun Draw(results: List<PlaceWeatherData>, isMetric: Boolean) {
        TemplateWidget(
            width = "max",
            height = "40%",
            position = "left",
            padding = "20,10,20,10",
            color = Colors.widgetBackground
        ) {
            val resultA = results[0]
            val resultB = results[1]
            val dayListA = resultA.getRelativeDailyData(dailyRange)
            val dayListB = resultB.getRelativeDailyData(dailyRange)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 25.dp, top = 10.dp)
                    .testTag("dailyComparisonWidget")
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Clock Icon",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 5.dp),
                        tint = Colors.widgetForeground,
                    )
                    Text(stringResource(R.string.daily_compare_widget), color = Colors.widgetForeground)
                }

                // Find the current day index to display first this hour in the scroll
                var startIndex: Int = 0

                // calculate the start index based on the queried day
                try {
                    val startIndexA = indexOfDateInDays(dayListA, resultA.queryDate)
                    val startIndexB = indexOfDateInDays(dayListB, resultB.queryDate)
                    if (startIndexA == startIndexB) {
                        startIndex = startIndexA
                    } else {
                        Log.e("DailyComparisonError", "Query date index mismatch")
                    }
                } catch (e: NoSuchElementException) {
                    Log.e("ScrollError", "Current hour not found in hourList: ${e.message}")
                }

                val scrollState = rememberScrollState()

                val dateSelected = GlobalVariables.primaryDate.value
                val widgetHeight = heightRatioToPx(
                    0.08f,
                    configuration = LocalConfiguration.current,
                    density = LocalDensity.current
                )


                // when the date changes or the start index changes, scroll to the new position accordingly
                LaunchedEffect(dateSelected, startIndex) {
                    scrollState.scrollTo((widgetHeight * startIndex).toInt())
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        for ((dayA, dayB) in dayListA.zip(dayListB)) {
                            // both queried days should be at the same index
                            val color = if (dayA.date < resultA.queryDate) {
                                Colors.subWidgetBackgroundVariant
                            } else {
                                Colors.subWidgetBackground
                            }
                            DailyComparisonWidgetSmall(dayA, dayB, color, isMetric)
                        }
                    }
                }
            }
        }
    }
}