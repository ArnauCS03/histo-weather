package com.example.histoweather.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.histoweather.data.favourites.FavouritesDao
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDao
import com.example.histoweather.data.searches.SearchesDataModel
import com.example.histoweather.data.settings.SettingsDao
import com.example.histoweather.data.settings.SettingsDataModel

/** Database file
 *  Main access point to the Room database.
 *  Configures the database and provides the DAO for database interactions.
 **/

@Database(entities = [SettingsDataModel::class, FavouritesDataModel::class, SearchesDataModel::class], version = 1, exportSchema = false)
abstract class DataModelDatabase: RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun favouritesDao(): FavouritesDao
    abstract fun searchesDao(): SearchesDao

    companion object {
        @Volatile
        private var INSTANCE: DataModelDatabase? = null

        /**
         * Provides the instance of the database, ensuring a single instance is created.
         */
        fun getDatabase(context: Context): DataModelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataModelDatabase::class.java,
                    "settings_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}