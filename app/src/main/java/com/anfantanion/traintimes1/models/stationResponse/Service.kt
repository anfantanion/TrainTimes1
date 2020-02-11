package com.anfantanion.traintimes1.models.stationResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class Service(
    val atocCode: String,
    val atocName: String,
    val isPassenger: Boolean,
    val locations: List<LocationDetail>,
    val runDate: String,
    val runningIdentity: String,
    val serviceType: String,
    val serviceUid: String,
    val trainIdentity: String
)
{
    fun time():String{
        return locations[0].gbttBookedDeparture
    }

    fun destination():String{
        return locations[0].destination[0].description
    }

    fun status():String{
        when (locations[0].serviceLocation){
            "APPR_STAT" -> return "Approaching Station"
            "APPR_PLAT" -> return "Arriving"
            "AT_PLAT" -> return "At Platform"
        }
        return ""
    }
}