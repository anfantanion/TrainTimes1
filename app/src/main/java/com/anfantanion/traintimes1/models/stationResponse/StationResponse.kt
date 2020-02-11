package com.anfantanion.traintimes1.models.stationResponse

data class StationResponse(
    val filter: Destination?,
    val location: Location,
    val services: List<Service>
){
    val timestamp = System.currentTimeMillis()
}