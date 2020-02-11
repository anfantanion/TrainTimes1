package com.anfantanion.traintimes1.repositories.cachedb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(CStationResponse::class),version = 2)
@TypeConverters(Converters::class)
abstract class CacheDatabase : RoomDatabase(){
    abstract fun CStationResponceDao() : CStationResponceDao
}