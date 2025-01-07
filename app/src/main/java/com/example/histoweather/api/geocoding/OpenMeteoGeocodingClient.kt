package com.example.histoweather.api.geocoding

import com.example.histoweather.api.OpenMeteoParameters
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface GeocodingClient {
    /**
     * Gets a list of search Results for a name
     */
    @Throws(Exception::class)
    suspend fun getByName(name: String): Search

    /**
     * Retrieves a Place, by its unique ID in OpenMeteo
     */
    @Throws(Exception::class)
    suspend fun getById(id: Int): Place
}

/**
 * Object containing properties of a search result
 * Not complete, properties can be added
 * Some properties can be null
 */
@Serializable
data class Place(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("elevation")
    val elevation: Float? = null,

    @SerialName("timezone")
    val timezone: String? = null,

    @SerialName("country_code")
    val countryCode: String? = null,

    @SerialName("country")
    val country: String? = null,

    @SerialName("admin1")
    val admin1: String? = null,
) {
    /**
     * ID is the only unique identifier for a place
     * This function allows us to use list functions like contains
     */
    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is Place) return false
        return id == other.id
    }

    /**
     * ID is the only unique identifier for a place
     * This function allows us to use Hash data structures
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * Query result containing a list of places
 */
@Serializable
data class Search(
    @SerialName("results")
    val results: List<Place>,
)

class OpenMeteoGeocodingClient: GeocodingClient {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Gets a list of search Results for a name
     */
    @Throws(Exception::class)
    override suspend fun getByName(name: String): Search {
        val encodedName = name.replace(" ", "+")
        val callURL = OpenMeteoParameters.baseURLs["geocoding"] +
                "name=$encodedName&count=10&language=en&format=json"
        val search: Search = httpClient.get(callURL).body()
        return search
    }

    /**
     * Retrieves a Place, by its unique ID in OpenMeteo
     */
    @Throws(Exception::class)
    override suspend fun getById(id: Int): Place {
        val callURL = OpenMeteoParameters.baseURLs["geoid"] + "id=$id"
        val result: Place = httpClient.get(callURL).body()
        return result
    }
}