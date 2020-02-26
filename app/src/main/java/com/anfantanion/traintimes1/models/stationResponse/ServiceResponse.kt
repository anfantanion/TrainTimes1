package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub

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

    fun getName(): String{
        return origin[0].publicTime +" to " +destination[0].description
    }

    fun getStationArrival(stationStub: StationStub) : String? {
        val filtered = locations.filter{ it.crs == stationStub.crs }
        return filtered[0].gbttBookedArrival
    }

    fun toServiceStub() : ServiceStub{
        return ServiceStub(serviceUid,runDate)
    }

}