package com.example.histoweather.ui.widgets


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.histoweather.api.OpenMeteoParameters
import com.example.histoweather.api.weather.PlaceWeatherData
import com.example.histoweather.api.weather.WeatherClient
import com.example.histoweather.ui.theme.Colors
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Current Weather Widget
 */
class CurrentWidget: Widget {
    val dailyRange = Pair(0L, 0L)

    /**
     * Constructor adds the required parameters to the OpenMeteoClient
     */
    constructor(omc: WeatherClient) : super(omc) {
        omc.addCurrentRequirements(
            listOf(
                "temperature_2m",
                "weather_code",
                "apparent_temperature",
            )
        )
        omc.addDailyRequirements(
            listOf(
                "temperature_2m_min",
                "temperature_2m_max",
            )
        )
        omc.addDailyRange(dailyRange)
    }

    /**
     * Draw the Current Weather Widget
     * @param result a PlaceWeatherData object resulting from the earlier given OpenMeteoClient
     */
    @Composable
    override fun Draw(result: List<PlaceWeatherData>, isMetric: Boolean) {
        val result = result[0]
        val temperature = result.current?.properties["temperature_2m"]
        val apparentTemperature = result.current?.properties["apparent_temperature"]?.toFloat()
            ?.roundToInt()
        val currentDay = result.getDayData(LocalDate.now())
        val maxTemperature = currentDay?.properties["temperature_2m_max"]
        val minTemperature = currentDay?.properties["temperature_2m_min"]

        TemplateWidget(
            width = "max",
            height = "20%",
            position = "center",
            padding = "20,10,20,10",
            color = Colors.widgetBackground,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp),
                verticalArrangement = Arrangement.Center
            ) {
                val symbol = if (isMetric) "C" else "F"

                // Temperature
                Text(
                    text = "$temperature°$symbol",
                    color = Colors.widgetForeground,
                    fontSize = 32.sp,
                    modifier = Modifier.testTag("currentWidget_current_temperature")
                )

                // Apparent Temperature
                Text(
                    text = "Feels like $apparentTemperature°$symbol",
                    color = Colors.widgetForeground,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp).testTag("currentWidget_apparent_temperature")
                )

                // High Low temperature
                Text(
                    text = "High $maxTemperature°$symbol | Low $minTemperature°$symbol",
                    color = Colors.widgetForeground,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp).testTag("currentWidget_high_low_temperature")
                )
            }

            // Image current weather on the right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 25.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                val icon =
                    OpenMeteoParameters.weatherCodesMap[result.current?.properties["weather_code"]]
                        ?: "?"
                Text(text = icon, fontSize = 60.sp, modifier = Modifier.testTag("currentWidget_weather_icon"))
            }

        }
    }
}
