package com.anfantanion.traintimes1.models.stationResponse

data class Service(
    val atocCode: String,
    val atocName: String,
    val isPassenger: Boolean,
    val locationDetail: LocationDetail,
    val runDate: String,
    val runningIdentity: String,
    val serviceType: String,
    val serviceUid: String,
    val trainIdentity: String
){



}