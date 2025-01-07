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
import androidx.compose.material.icons.filled.DateRange
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
import com.example.histoweather.api.weather.Day
import com.example.histoweather.api.weather.PlaceWeatherData
import com.example.histoweather.api.weather.WeatherClient
import com.example.histoweather.ui.indexOfDateInDays
import com.example.histoweather.ui.localDateToString
import com.example.histoweather.ui.precipitationToString
import com.example.histoweather.ui.theme.Colors
import com.example.histoweather.ui.widthRatioToPx
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Ten Day Widget
 */
class TenDayWidget : Widget {
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
     * Widget for a single day in the scrollable widget
     */
    @Composable
    fun DayWidget(day: Day, color: Color, isMetric: Boolean) {

        TemplateWidget(
            width = "15%",
            height = "20%",
            position = "center",
            padding = "0,5,5,0",
            color = color,
            outline = LocalDate.now() == day.date,
            outlineColor = Colors.subWidgetOutline,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,

                modifier = Modifier
                    .fillMaxSize()
            ) {
                val temperature = day.properties["temperature_2m_mean"]?.toFloat()?.roundToInt()
                val symbol = if (isMetric) "C" else "F"
                val icon =
                    OpenMeteoParameters.weatherCodesMap[day.properties["weather_code"]] ?: "?"
                // includeYear can be set to true if needed, I think, it just gets too crowded
                val date = localDateToString(day.date, includeYear = false)
                val precipitation = precipitationToString(
                    day.properties["precipitation_sum"]?.toFloat(),
                    isMetric
                )

                Text(date, textAlign = TextAlign.Center, color = Colors.subWidgetForeground)
                Text(
                    text = icon,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Colors.subWidgetForeground,
                    modifier = Modifier.testTag("dayWidget_icon_$date")
                )
                Text(
                    precipitation,
                    textAlign = TextAlign.Center,
                    color = Colors.precipitation,
                    modifier = Modifier.testTag("dayWidget_precipitation_$date")
                )
                Text(
                    "${temperature}°$symbol",
                    textAlign = TextAlign.Center,
                    color = Colors.subWidgetForeground,
                    modifier = Modifier.testTag("dayWidget_temperature_$date")
                )
            }
        }
    }

    /**
     * Draw the Ten Day Widget. This widget displays the 10-day forecast in a horizontal scrollable list.
     * @param result a PlaceWeatherData object resulting from the earlier given OpenMeteoClient
     */
    @Composable
    override fun Draw(results: List<PlaceWeatherData>, isMetric: Boolean) {
        TemplateWidget(
            width = "max",
            height = "27%",
            position = "center",
            padding = "20,10,20,10",
            color = Colors.widgetBackground,
        ) {
            val result = results[0]
            val daysList = result.getRelativeDailyData(dailyRange)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 25.dp, top = 10.dp, end = 25.dp, bottom = 25.dp)
                    .testTag("dayWidget")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Clock Icon",
                        modifier = Modifier
                            .size(28.dp)
                            .padding(top = 10.dp),
                        tint = Colors.widgetForeground,
                    )
                    Text(
                        stringResource(R.string.daily_widget),
                        modifier = Modifier.padding(top = 10.dp),
                        color = Colors.widgetForeground,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Find the current day index to display first this hour in the scroll
                var startIndex: Int = 0

                // calculate the start index based on the queried day
                try {
                    startIndex = indexOfDateInDays(daysList, result.queryDate)
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
                    scrollState.scrollTo((widgetWidth * startIndex).toInt())
                }

                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                ) {
                    for (day in daysList) {
                        val color = if (day.date < result.queryDate)
                            Colors.subWidgetBackgroundVariant
                        else
                            Colors.subWidgetBackground
                        DayWidget(day, color, isMetric)
                    }
                }
            }
        }
    }
}