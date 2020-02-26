package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.differenceOfTimes
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

data class LocationDetail(
    val realtimeActivated: Boolean,
    val tiploc: String,
    val crs: String,
    val description: String,
    //val wttBookedArrival : String?,
    //val wttBookedDeparture : String?,
    //val wttBookedPass  : String?,
    val gbttBookedArrival: String?,
    val gbttBookedDeparture: String?,
    var origin: List<Origin>,
    var destination: List<Destination>,
    val isCall: Boolean,
    val isPublicCall: Boolean,
    val realtimeArrival: String?,
    val realtimeArrivalActual: Boolean?,
    val realtimeDeparture: String?,
    val realtimeDepartureActual: Boolean?,
    val realtimeDepartureNoReport: Boolean?,
    val displayAs: String,
    val line: String?,
    val lineConfirmed: Boolean?,
    val path: String?,
    val pathConfirmed: Boolean?,
    val platform: String?,
    val platformChanged: Boolean?,
    val platformConfirmed: Boolean?,
    val serviceLocation: String?,
    val associations: List<Association>?,
    val cancelReasonCode: String?,
    val cancelReasonShortText: String?,
    val cancelReasonLongText: String?
){

    fun getArrivalTime(): String?{
        if (realtimeArrival != null) return realtimeArrival
        return gbttBookedArrival
    }

    fun getDepartureTime(): String?{
        if (realtimeDeparture != null) return realtimeDeparture
        return gbttBookedDeparture
    }

    fun destinationName():String{
        return com.anfantanion.traintimes1.models.destinationName(destination)
    }


    fun delayString() : String {
        var delay = delay()
        if (delay==null || delay==0) return ""
        else return "$delay"

    }

    fun delay() : Int? {

        return if (realtimeDeparture != null && gbttBookedDeparture != null){
            differenceOfTimes(realtimeDeparture,gbttBookedDeparture)
        }else null
    }

    fun statusString():String{
        when (displayAs){
            "CANCELLED_CALL" -> {return "Cancelled (${cancelReasonCode}) - ${cancelReasonShortText?.capitalize()}"}
            "STARTS" , "ORIGIN" -> {}
        }
        when (serviceLocation){ // Departed at?
            "APPR_STAT" -> return "Approaching Station"
            "APPR_PLAT" -> return "Arriving"
            "AT_PLAT" -> return "At Platform"
        }
        if (delay()?:0 > 5)
            return "Expected at "+realtimeDeparture
        return "On Time"
    }


}