package com.example.histoweather.test

import com.example.histoweather.api.geocoding.GeocodingClient
import com.example.histoweather.api.geocoding.Place
import com.example.histoweather.api.geocoding.Search

class MockGeocoding: GeocodingClient {
    val places = listOf<Place>(
        Place(295015934, "Berlin", 52.52, 13.405, 74.0f, "Europe/Berlin", "DE", "Germany", "Berlin"),
        Place(291129832, "Hamburg", 53.55, 9.993, 6.0f, "Europe/Berlin", "DE", "Germany", "Hamburg"),
        Place(286771423, "Munich", 48.137, 11.575, 520.0f, "Europe/Berlin", "DE", "Germany", "Bavaria"),
        Place(288624223, "Cologne", 50.937, 6.957, 37.0f, "Europe/Berlin", "DE", "Germany", "North Rhine-Westphalia"),
        Place(292553655, "Frankfurt", 50.115, 8.683, 112.0f, "Europe/Berlin", "DE", "Germany", "Hesse"),
        Place(282529755, "Stuttgart", 48.775, 9.182, 264.0f, "Europe/Berlin", "DE", "Germany", "Baden-Württemberg"),
    )

    override suspend fun getByName(name: String): Search {
        return Search(places.filter { it.name.lowercase().startsWith(name.lowercase()) })
    }

    override suspend fun getById(id: Int): Place {
        // return place or throw error
        return places.find { it.id == id } ?: throw Exception("Place not found")
    }
}