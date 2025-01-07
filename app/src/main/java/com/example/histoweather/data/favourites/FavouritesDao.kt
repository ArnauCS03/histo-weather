package com.example.histoweather.data.favourites
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface FavouritesDao {

    @Query("SELECT * FROM favourites")
    suspend fun getAllFavourites(): List<FavouritesDataModel>

    @Upsert
    suspend fun upsertFavourite(favourites: List<FavouritesDataModel>)

    @Query("DELETE FROM favourites WHERE city = :city AND countryCode = :countryCode AND idAPI = :idAPI")
    suspend fun deleteFavourite(city: String, countryCode: String, idAPI: Int)


    @Query("SELECT COUNT(*) FROM favourites")
    suspend fun countFavourites(): Int
}