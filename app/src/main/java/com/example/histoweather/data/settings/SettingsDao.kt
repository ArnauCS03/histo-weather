package com.example.histoweather.data.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SettingsDao {

    @Query("SELECT temperatureUnit FROM settings WHERE id = 1")
    suspend fun isMetric(): Boolean?

    @Query("SELECT theme FROM settings WHERE id = 1")
    suspend fun getTheme(): String?

    @Query("UPDATE settings SET theme = :themeMode WHERE id = 1")
    suspend fun updateTheme(themeMode: String)

    @Query("UPDATE settings SET temperatureUnit = :unit WHERE id = 1")
    suspend fun updateTemperatureUnit(unit: String)

    @Query("SELECT COUNT(*) FROM settings")
    suspend fun getSettingsCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSettings(settings: SettingsDataModel)

    /**
     * Deletes all data from the settings table.
     */
    @Query("DELETE FROM settings")
    suspend fun clearDataModel()
}