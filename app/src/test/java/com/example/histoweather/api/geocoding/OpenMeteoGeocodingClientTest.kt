package com.example.histoweather.api.geocoding

import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OpenMeteoGeocodingClientTest {
    @Test
    fun placeEquals_same() {
        val place = Place(
            id = -1,
            name = "name",
            latitude = 20.001,
            longitude = 15.012,
        )
        assertEquals(place, place)
    }

    @Test
    fun placeEquals_sameId() {
        val place1 = Place(
            id = 0,
            name = "name",
            latitude = 20.001,
            longitude = 15.012,
        )
        val place2 = Place(
            id = 0,
            name = "renamed",
            latitude = 20.000,
            longitude = 15.000,
        )
        assertEquals(place1, place2)
    }

    @Test
    fun placeEquals_differentId() {

        val place1 = Place(
            id = 0,
            name = "name",
            latitude = 20.001,
            longitude = 15.012,
        )
        val place2 = Place(
            id = 1,
            name = "renamed",
            latitude = 20.000,
            longitude = 15.000,
        )
        assertNotEquals(place1, place2)
    }

    @Test
    fun placeHash_sameId(){
        val place1 = Place(
            id = 0,
            name = "name",
            latitude = 20.001,
            longitude = 15.012,
        )
        val place2 = Place(
            id = 0,
            name = "renamed",
            latitude = 20.000,
            longitude = 15.000,
        )
        assertEquals(place1.hashCode(), place2.hashCode())
    }

    @Test
    fun placeHash_differentId(){
        val place1 = Place(
            id = 0,
            name = "name",
            latitude = 20.001,
            longitude = 15.012,
        )
        val place2 = Place(
            id = 1,
            name = "renamed",
            latitude = 20.000,
            longitude = 15.000,
        )
        assertNotEquals(place1.hashCode(), place2.hashCode())
    }
}