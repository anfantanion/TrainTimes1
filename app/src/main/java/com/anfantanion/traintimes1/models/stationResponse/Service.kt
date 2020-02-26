package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.repositories.StationRepo
import java.lang.StringBuilder


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
)
{
    fun time():String{
        return locationDetail.gbttBookedDeparture ?: "----"
    }

    fun destination():String{
        return locationDetail.destinationName()
    }

    fun statusString():String{
        return locationDetail.statusString()
    }

    fun platform():String{
        return when (serviceType){
            "train" -> locationDetail.platform?: ""
            "bus" -> "Bus"
            else -> ""
        }
    }

    fun toServiceStub() : ServiceStub{
        return ServiceStub(serviceUid,runDate)
    }
}