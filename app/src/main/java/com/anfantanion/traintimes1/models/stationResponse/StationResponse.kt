package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.TimeDate
import java.util.*

data class StationResponse(
    val filter: Filter?,
    val location: Location,
    val services: List<Service>?,
    val to : String?,
    val from : String?,
    val date : Date?,
    val error: String?,
    val errcode: String?
){

    fun nextService(timeDate: TimeDate, leeway: Int = 0): Service?{
        return services?.filter {
            timeDate.getTime().toInt()+leeway < it.locationDetail.gbttBookedDeparture!!.toInt()
        }?.get(0)
    }
}