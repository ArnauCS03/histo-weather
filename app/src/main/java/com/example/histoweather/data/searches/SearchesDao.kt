package com.example.histoweather.data.searches

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchesDao {

    @Query("SELECT * FROM searches")
    suspend fun getAllSearches(): List<SearchesDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearches(searches: List<SearchesDataModel>)


    @Query("DELETE FROM searches WHERE city = :city AND countryCode = :countryCode AND idAPI = :idAPI")
    suspend fun deleteSearch(city: String, countryCode: String, idAPI: Int)


    @Query("SELECT COUNT(*) FROM searches")
    suspend fun countSearches(): Int
}