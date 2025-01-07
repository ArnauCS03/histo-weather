package com.example.histoweather.api.weather
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class OpenMeteoClientTest {

        @Test
        fun addHourlyRange_sameRange() {
            val client = OpenMeteoClient()
            client.addHourlyRange(Pair(2L, 4L))
            client.addHourlyRange(Pair(2L, 4L))
            assertEquals(Pair(2L, 4L), client.hourlyRange)
        }

        @Test
        fun addHourlyRange_overlapping() {
            val client = OpenMeteoClient()
            client.addHourlyRange(Pair(2, 4))
            client.addHourlyRange(Pair(3, 6))
            assertEquals(Pair(2L, 6L), client.hourlyRange)
        }

        @Test
        fun addHourlyRange_inside() {
            val client = OpenMeteoClient()
            client.addHourlyRange(Pair(2, 6))
            client.addHourlyRange(Pair(3, 5))
            assertEquals(Pair(2L, 6L), client.hourlyRange)
        }

        @Test
        fun addHourlyRange_disjoint() {
            val client = OpenMeteoClient()
            client.addHourlyRange(Pair(2, 4))
            client.addHourlyRange(Pair(6, 8))
            assertEquals(Pair(2L, 8L), client.hourlyRange)
        }

        @Test
        fun addDailyRange_sameRange() {
            val client = OpenMeteoClient()
            client.addDailyRange(Pair(2, 4))
            client.addDailyRange(Pair(2, 4))
            assertEquals(Pair(2L, 4L), client.dailyRange)
        }

        @Test
        fun addDailyRange_overlapping() {
            val client = OpenMeteoClient()
            client.addDailyRange(Pair(1, 3))
            client.addDailyRange(Pair(2, 5))
            assertEquals(Pair(1L, 5L), client.dailyRange)
        }

        @Test
        fun addDailyRange_inside() {
            val client = OpenMeteoClient()
            client.addDailyRange(Pair(1, 6))
            client.addDailyRange(Pair(3, 5))
            assertEquals(Pair(1L, 6L), client.dailyRange)
        }

        @Test
        fun addDailyRange_disjoint() {
            val client = OpenMeteoClient()
            client.addDailyRange(Pair(1, 3))
            client.addDailyRange(Pair(5, 7))
            assertEquals(Pair(1L, 7L), client.dailyRange)
        }

        @Test
        fun addHourlyRequirement_validForecastParameter() {
            val client = OpenMeteoClient()
            val result = client.addHourlyRequirement("temperature_2m")
            assertEquals(true, result)
            assertEquals(listOf("temperature_2m"), client.forecastHourlyRequirements)
        }

        @Test
        fun addHourlyRequirement_validHistoricalParameter() {
            val client = OpenMeteoClient()
            val result = client.addHourlyRequirement("relative_humidity_2m")
            assertEquals(true, result)
            assertEquals(listOf("relative_humidity_2m"), client.historicalHourlyRequirements)
        }

        @Test
        fun addHourlyRequirement_invalidParameter() {
            val client = OpenMeteoClient()
            val result = client.addHourlyRequirement("invalid_param")
            assertEquals(false, result)
            assertEquals(emptyList<String>(), client.forecastHourlyRequirements)
        }

        @Test
        fun containsHistoricalRange_validRange() {
            val client = OpenMeteoClient()
            val histEnd = LocalDate.now().minusDays(5)
            val result = client.containsHistoricalRange(histEnd.minusDays(3), histEnd)
            assertEquals(true, result)
        }

        @Test
        fun containsHistoricalRange_invalidRange() {
            val client = OpenMeteoClient()
            val histEnd = LocalDate.now().minusDays(5)
            val result = client.containsHistoricalRange(histEnd.plusDays(1), histEnd.plusDays(2))
            assertEquals(false, result)
        }

        @Test
        fun toHistRange_valid() {
            val client = OpenMeteoClient()
            val histEnd = LocalDate.now().minusDays(5)
            val result = client.toHistRange(histEnd.minusDays(3), histEnd.plusDays(2))
            assertEquals(Pair(histEnd.minusDays(3), histEnd), result)
        }

        @Test
        fun toHistRange_invalid() {
            val client = OpenMeteoClient()
            val histEnd = LocalDate.now().minusDays(5)
            assertThrows(IllegalArgumentException::class.java) {
                client.toHistRange(histEnd.plusDays(1), histEnd.plusDays(2))
            }
        }

        @Test
        fun toForecastRange_valid() {
            val client = OpenMeteoClient()
            val histEnd = LocalDate.now().minusDays(5)
            val result = client.toForecastRange(histEnd.minusDays(3), histEnd.plusDays(2))
            assertEquals(Pair(histEnd.plusDays(1), histEnd.plusDays(2)), result)
        }

        @Test
        fun toForecastRange_invalid() {
            val client = OpenMeteoClient()
            val histEnd = LocalDate.now().minusDays(5)
            assertThrows(IllegalArgumentException::class.java) {
                client.toForecastRange(histEnd.minusDays(6), histEnd.minusDays(5))
            }
        }

        @Test
        fun constructCurrentURL_valid() {
            val client = OpenMeteoClient()
            client.addCurrentRequirements(listOf("temperature_2m", "relative_humidity_2m"))
            client.setMetric()
            val result = client.constructCurrentURL(52.52, 13.41)
            assertEquals(
                "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&timezone=auto&current=temperature_2m,relative_humidity_2m,&temperature_unit=celsius&wind_speed_unit=kmh&precipitation_unit=mm",
                result
            )
        }

        @Test
        fun constructDailyForecastURL_valid() {
            val client = OpenMeteoClient()
            client.addDailyRequirement("temperature_2m_mean")
            client.setMetric()
            val result = client.constructDailyForecastURL(52.52, 13.41, LocalDate.of(2024, 11, 10), LocalDate.of(2024, 11, 15))
            assertEquals(
                "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&start_date=2024-11-10&end_date=2024-11-15&timezone=auto&daily=temperature_2m_mean,&temperature_unit=celsius&wind_speed_unit=kmh&precipitation_unit=mm",
                result
            )
        }
    }

