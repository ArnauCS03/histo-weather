package com.example.histoweather.ui.widgets

import com.example.histoweather.api.weather.OpenMeteoClient
import com.example.histoweather.api.weather.WeatherClient
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class WidgetsTest {
    fun <T>assertContains(list: List<T>, expected: List<T>) {
        for (item in expected) {
            assertTrue(list.contains(item))
        }
    }

    fun <T>assertContainedInBoth(list1: List<T>, list2: List<T>, expected: List<T>) {
        for (item in expected) {
            assertTrue(list1.contains(item) && list2.contains(item))
        }
    }

    fun assertContainsRange(range: Pair<Long, Long>, expected: Pair<Long, Long>) {
        assertTrue(range.first <= expected.first)
        assertTrue(range.second >= expected.second)
    }

    @Test
    fun currentWidget_realClientInit() {
        val client = OpenMeteoClient()
        CurrentWidget(client)
        assertContains(client.currentRequirements, listOf("temperature_2m", "weather_code", "apparent_temperature"))
        assertContains(client.forecastDailyRequirements, listOf("temperature_2m_min", "temperature_2m_max"))
        assertContainsRange(client.dailyRange, Pair(0L, 0L))
    }

    @Test
    fun hourlyWidget_realClientInit() {
        val client = OpenMeteoClient()
        HourlyWidget(client)
        assertContainedInBoth(client.historicalHourlyRequirements, client.forecastHourlyRequirements, listOf("temperature_2m", "weather_code", "precipitation"))
        assertContains(client.forecastHourlyRequirements, listOf("precipitation_probability"))
        assertContainsRange(client.hourlyRange, Pair(0L, 1L))
    }

    @Test
    fun dailyWidget_realClientInit() {
        val client = OpenMeteoClient()
        TenDayWidget(client)
        assertContainedInBoth(client.historicalDailyRequirements, client.forecastDailyRequirements, listOf("temperature_2m_mean", "weather_code", "precipitation_sum"))
        assertContainsRange(client.dailyRange, Pair(-3L, 10L))
    }

    @Test
    fun hourlyComparisonWidget_realClientInit() {
        val client = OpenMeteoClient()
        HourlyComparisonWidget(client)
        assertContainedInBoth(client.historicalHourlyRequirements, client.forecastHourlyRequirements, listOf("temperature_2m", "weather_code", "precipitation"))
        assertContainsRange(client.hourlyRange, Pair(0L, 1L))
    }

    @Test
    fun dailyComparisonWidget_realClientInit() {
        val client = OpenMeteoClient()
        DailyComparisonWidget(client)
        assertContainedInBoth(client.historicalDailyRequirements, client.forecastDailyRequirements, listOf("temperature_2m_mean", "weather_code", "precipitation_sum"))
        assertContainsRange(client.dailyRange, Pair(-3L, 10L))
    }

    @Test
    fun allWidgets_realClientInit() {
        val client = OpenMeteoClient()
        // this is the way, the Page function calls the widgets
        val widgetClasses = listOf(
            CurrentWidget::class.java,
            HourlyWidget::class.java,
            TenDayWidget::class.java,
            HourlyComparisonWidget::class.java,
            DailyComparisonWidget::class.java,
        )
        for (widgetClass in widgetClasses) {
            widgetClass.getConstructor(WeatherClient::class.java).newInstance(client)
        }
        // assert, that the lists are exactly the expected ones
        val expectedCurrentRequirements = listOf("temperature_2m", "weather_code", "apparent_temperature")
        val expectedForecastDailyRequirements = listOf("temperature_2m_min", "temperature_2m_max", "temperature_2m_mean", "weather_code", "precipitation_sum")
        val expectedHistoricalHourlyRequirements = listOf("temperature_2m", "weather_code", "precipitation")
        val expectedForecastHourlyRequirements = listOf("temperature_2m", "weather_code", "precipitation_probability", "precipitation")
        val expectedHistoricalDailyRequirements = listOf("temperature_2m_min", "temperature_2m_max", "temperature_2m_mean", "weather_code", "precipitation_sum")
        assertEquals(expectedCurrentRequirements, client.currentRequirements)
        assertEquals(expectedForecastDailyRequirements, client.forecastDailyRequirements)
        assertEquals(expectedHistoricalHourlyRequirements, client.historicalHourlyRequirements)
        assertEquals(expectedForecastHourlyRequirements, client.forecastHourlyRequirements)
        assertEquals(expectedHistoricalDailyRequirements, client.historicalDailyRequirements)
    }
}