package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.destinationName
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import java.lang.StringBuilder

data class ServiceResponse(
    val atocCode: String,
    val atocName: String,
    val isPassenger: Boolean,
    var locations: List<LocationDetail>,
    var origin: List<Origin>,
    var destination: List<Destination>,
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
        return origin[0].publicTime +" to " + destinationName(destination)
    }

    fun getStationArrival(stationStub: StationStub) : String? {
        val filtered = locations.filter{ it.crs == stationStub.crs }
        return filtered[0].gbttBookedArrival
    }

    fun toServiceStub() : ServiceStub{
        return ServiceStub(serviceUid,runDate)
    }

}