package com.anfantanion.traintimes1.models.stationResponse

data class ServiceResponse(
    val atocCode: String,
    val atocName: String,
    val destination: List<Destination>,
    val isPassenger: Boolean,
    val locations: List<LocationDetail>,
    val origin: List<Origin>,
    val performanceMonitored: Boolean,
    val powerType: String,
    val realtimeActivated: Boolean,
    val runDate: String,
    val runningIdentity: String,
    val serviceType: String,
    val serviceUid: String,
    val trainClass: String,
    val trainIdentity: String
){
    val timestamp = System.currentTimeMillis()
}