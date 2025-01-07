
package com.example.histoweather.data.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a table in the Room database.
 * Each instance of this class corresponds to a row in the "DataModel" table.
 */
@Entity(tableName = "settings")
data class SettingsDataModel(
    @PrimaryKey val id: Int = 1,
    val temperatureUnit: Boolean = true,  // true for Celsius (metric), false for Fahrenheit (imperial)
    val theme: String = "system"        // "dark", "light", or "system"
)

// Example of the table:

//      ID     temperatureUnit      theme
//       1        true              "dark"
