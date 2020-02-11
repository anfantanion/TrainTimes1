package com.anfantanion.traintimes1.repositories.cachedb

import androidx.room.TypeConverter
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.google.gson.Gson

class Converters {

    val gson = Gson()

    @TypeConverter
    fun fromJson(json : String) : StationResponse{
        return gson.fromJson<StationResponse>(json,StationResponse::class.java)
    }

    @TypeConverter
    fun toJson(stationResponse: StationResponse) : String{
        return gson.toJson(stationResponse)
    }
}