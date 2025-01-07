package com.example.histoweather.data.favourites

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favourites", indices = [Index(value = ["idAPI"], unique = true)])
class FavouritesDataModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idAPI: Int,
    val city: String,
    val countryCode: String,
)

// Example of the table:

//      Id    idAPI     city      countryCode
//      0    1234567    London       UK