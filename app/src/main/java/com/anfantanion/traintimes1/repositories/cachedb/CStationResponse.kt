package com.anfantanion.traintimes1.repositories.cachedb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import java.util.*

@Entity
data class CStationResponse(
    @ColumnInfo(name = "stationCode")
    val stationCode : String,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "cto")
    var cto: String?,

    @ColumnInfo(name = "cfrom")
    var cfrom: String?,

    @ColumnInfo(name = "time_filter")
    var time_filter: String?,

    @ColumnInfo(name = "object")
    var cObject: StationResponse,

    @PrimaryKey(autoGenerate = true)
    val id : Int = 0


)