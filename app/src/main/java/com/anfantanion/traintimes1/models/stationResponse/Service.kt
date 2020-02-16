package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.parcelizable.ServiceStub


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
        return locationDetail.destination[0].description
    }

    fun status():String{
        when (locationDetail.serviceLocation){
            "APPR_STAT" -> return "Approaching Station"
            "APPR_PLAT" -> return "Arriving"
            "AT_PLAT" -> return "At Platform"
        }
        return "On Time"
    }

    fun platform():String{
        return when (serviceType){
            "train" -> locationDetail.platform?: ""
            "bus" -> "Bus"
            else -> ""
        }
    }

    fun toServiceStub() : ServiceStub{
        return ServiceStub(serviceUid,runDate.replace('-','/'))
    }
}