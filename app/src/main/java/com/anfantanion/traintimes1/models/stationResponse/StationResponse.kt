package com.anfantanion.traintimes1.models.stationResponse

import java.util.*

data class StationResponse(
    val filter: Filter,
    val location: Location,
    val services: List<Service>,
    val to : String?,
    val from : String?,
    val date : Date?,
    val error: String?,
    val errcode: String?
){

    val timestamp = System.currentTimeMillis()
}