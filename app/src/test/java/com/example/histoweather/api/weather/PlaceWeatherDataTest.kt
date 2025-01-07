package com.example.histoweather.api.weather

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.time.LocalDate

class PlaceWeatherDataTest {
    val resp = OpenMeteoResponse(
        latitude = "1.0",
        longitude = "2.0",
        current = mapOf(
            "time" to "2024-01-01T12:00",
            "temperature" to "20"
        ),
        hourly = mapOf(
            "time" to listOf("2024-01-01T13:00", "2024-01-01T14:00"),
            "temperature" to listOf("21", "22"),
        ),
        daily = mapOf(
            "time" to listOf("2024-01-01", "2024-01-02"),
            "temperature" to listOf("22", "23"),
        )
    )

    fun assertResponse(resp: OpenMeteoResponse, pwd: PlaceWeatherData) {
        assertEquals(resp.latitude, pwd.latitude)
        assertEquals(resp.longitude, pwd.longitude)
        assertEquals(resp.current?.get("time"), pwd.current?.time.toString())
        assertEquals(resp.current?.get("temperature"), pwd.current?.properties?.get("temperature"))
        assertEquals(resp.hourly?.get("time")?.size, pwd.hourList.size)
        assertEquals(resp.hourly?.get("time")?.size, pwd.hourList.size)
        assertEquals(resp.daily?.get("time")?.size, pwd.daysList.size)
        assertEquals(resp.daily?.get("time")?.size, pwd.daysList.size)
        if (resp.hourly != null || resp.daily != null || resp.current != null) {
            assertTrue(pwd.valid)
        } else {
            assertFalse(pwd.valid)
        }
    }

    @Test
    fun placeWeatherData_empty() {
        val pwd = PlaceWeatherData()
        assertEquals(null, pwd.latitude)
        assertEquals(null, pwd.longitude)
        assertEquals(null, pwd.current)
        assertEquals(0, pwd.hourList.size)
        assertEquals(0, pwd.daysList.size)
        assertFalse(pwd.valid)
    }

    @Test
    fun placeWeatherData_singleResponse() {
        val pwd = PlaceWeatherData(queryDate = LocalDate.of(2024, 1, 1), resp = resp)
        assertResponse(resp, pwd)
    }

    @Test
    fun placeWeatherData_appendResp() {
        val pwd = PlaceWeatherData()
        pwd.appendResp(resp)
        assertResponse(resp, pwd)
    }
}