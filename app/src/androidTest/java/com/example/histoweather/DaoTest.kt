package com.example.histoweather.persistenceTest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.histoweather.data.DataModelDatabase
import com.example.histoweather.data.favourites.FavouritesDao
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDao
import com.example.histoweather.data.searches.SearchesDataModel
import com.example.histoweather.data.settings.SettingsDao
import com.example.histoweather.data.settings.SettingsDataModel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Source for looking up on how to test a Room database (last opened at 06.01.2025)
 * https://medium.com/huawei-developers/how-to-test-room-database-in-jetpack-compose-6520930e67f5
 */
@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var favouritesDao: FavouritesDao
    private lateinit var searchesDao: SearchesDao
    private lateinit var settingsDao: SettingsDao

    private lateinit var database: DataModelDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, DataModelDatabase::class.java
        ).build()
        favouritesDao = database.favouritesDao()
        searchesDao = database.searchesDao()
        settingsDao = database.settingsDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    // Testing Favourites
    @Test
    fun favouritesDao_insert_and_retrieve() = runTest {

        val favourite =
            FavouritesDataModel(id = 0, idAPI = 2950156, city = "Berlin", countryCode = "DE")

        favouritesDao.upsertFavourite(listOf(favourite))

        val retrieved = favouritesDao.getAllFavourites()
        assertEquals(1, retrieved.size)
        assertEquals(favourite.city, retrieved[0].city)
        assertEquals(favourite.countryCode, retrieved[0].countryCode)
    }

    @Test
    fun favourites_delete_and_count() = runTest(){

        val favourite2 = FavouritesDataModel(id = 1, idAPI = 2988507, city = "Paris", countryCode = "FR")

        favouritesDao.upsertFavourite(listOf(favourite2))
        val before = favouritesDao.countFavourites()

        favouritesDao.deleteFavourite("Paris", "FR", 2988507)
        val after = favouritesDao.countFavourites()

        val remainingFavourites = favouritesDao.getAllFavourites()
        assertFalse(remainingFavourites.contains(favourite2))
        assertTrue(before -1 == after)
    }

    // Testing recent Searches
    @Test
    fun searches_insert_and_retrieve() = runTest(){
        val searches =
            SearchesDataModel(id = 0, idAPI = 2950156, city = "Berlin", countryCode = "DE")

        searchesDao.upsertSearches(listOf(searches))

        val retrieved = searchesDao.getAllSearches()
        assertEquals(1, retrieved.size)
        assertEquals(searches.city, retrieved[0].city)
        assertEquals(searches.countryCode, retrieved[0].countryCode)
    }

    @Test
    fun delete_and_count_searches() = runTest(){
        val search2 = SearchesDataModel(id = 1, idAPI = 2988507, city = "Paris", countryCode = "FR")

        searchesDao.upsertSearches(listOf(search2))
        val before = searchesDao.countSearches()

        searchesDao.deleteSearch("Paris", "FR", 2988507)
        val after = searchesDao.countSearches()

        val remainingSearches = searchesDao.getAllSearches()
        assertFalse(remainingSearches.contains(search2))
        assertTrue(before -1 == after)
    }

    // Testing settings

    @Test
    fun insert_and_get_Settings() = runTest(){
        val setting = SettingsDataModel(id = 1, temperatureUnit = true, theme = "system")

        settingsDao.insertSettings(setting)
        val theme = settingsDao.getTheme()
        val metric = settingsDao.isMetric()
        assertEquals("system", theme)
        assertEquals(true, metric)
    }

    @Test
    fun updateTheme() = runTest {
        // Insert initial settings
        val setting = SettingsDataModel(id = 1, temperatureUnit = true, theme = "light")
        settingsDao.insertSettings(setting)

        // Update the theme
        settingsDao.updateTheme("dark")

        // Retrieve the updated theme and verify
        val updatedTheme = settingsDao.getTheme()
        assertEquals("dark", updatedTheme)
    }

    @Test
    fun updateTemperatureUnit() = runTest {
        // Insert initial settings
        val setting = SettingsDataModel(id = 1, temperatureUnit = true, theme = "light")
        settingsDao.insertSettings(setting)

        // Update the temperature unit
        settingsDao.updateTemperatureUnit("imperial")

        // Retrieve the updated temperature unit and verify
        val updatedUnit = settingsDao.isMetric()
        assertEquals(false, updatedUnit) // Assuming "imperial" is false and "metric" is true
    }

    @Test
    fun countSettings() = runTest {
        // Ensure database starts empty
        assertEquals(0, settingsDao.getSettingsCount())

        // Insert a settings row
        val setting = SettingsDataModel(id = 1, temperatureUnit = true, theme = "light")
        settingsDao.insertSettings(setting)

        // Verify count
        val count = settingsDao.getSettingsCount()
        assertEquals(1, count)
    }

    @Test
    fun insertSettings_withConflict() = runTest {
        // Insert initial settings
        val setting1 = SettingsDataModel(id = 1, temperatureUnit = true, theme = "light")
        settingsDao.insertSettings(setting1)

        // Attempt to insert conflicting settings (same id)
        val setting2 = SettingsDataModel(id = 1, temperatureUnit = false, theme = "dark")
        settingsDao.insertSettings(setting2)

        // Verify that the original settings are retained
        val theme = settingsDao.getTheme()
        val metric = settingsDao.isMetric()
        assertEquals("light", theme)
        assertEquals(true, metric)
    }


}