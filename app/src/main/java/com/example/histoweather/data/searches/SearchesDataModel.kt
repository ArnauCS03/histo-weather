package com.example.histoweather.data.searches

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "searches", indices = [Index(value = ["idAPI"], unique = true)])
data class SearchesDataModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idAPI: Int,
    val city: String,
    val countryCode: String
)

// Example of the table:

//      Id    idAPI     city      countryCode
//      0    1234567    Paris       FR