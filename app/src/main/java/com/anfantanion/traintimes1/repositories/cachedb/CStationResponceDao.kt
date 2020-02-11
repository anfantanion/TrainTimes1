package com.anfantanion.traintimes1.repositories.cachedb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CStationResponceDao {

    @Query(
        """
        SELECT * 
        FROM CStationResponse 
        WHERE stationCode==(:station) 
        AND cto == (:to)
        AND cfrom == (:from)
        AND time_filter == (:time_filter)
        AND (timestamp - (:currentTime)) < (:maxAge)
    """
    )
    fun getMatching(station : String,
                    to : String?,
                    from : String?,
                    time_filter : String?,
                    maxAge : Long,
                    currentTime: Long = System.currentTimeMillis()
                    ) : List<CStationResponse>

    @Insert
    fun insert(cStationResponse: CStationResponse)
}