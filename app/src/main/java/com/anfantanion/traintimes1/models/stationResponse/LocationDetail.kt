package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.TimeDate
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
    val origin: List<Origin>,
    val destination: List<Destination>,
    val isCall: Boolean,
    val isPublicCall: Boolean,
    val realtimeArrival: String?,
    val realtimeArrivalActual: Boolean?,
    val realtimeDeparture: String?,
    val realtimeDepartureActual: Boolean?,
    val displayAs: String,
    val line: String?,
    val lineConfirmed: Boolean?,
    val path: String?,
    val pathConfirmed: Boolean?,
    val platform: String?,
    val platformChanged: Boolean?,
    val platformConfirmed: Boolean?,
    val serviceLocation: String?,
    val associations: List<Association>?
){

    fun getArrivalTime(): String?{
        if (realtimeArrival != null) return realtimeArrival
        return gbttBookedArrival
    }

    fun getDepartureTime(): String?{
        if (realtimeDeparture != null) return realtimeDeparture
        return gbttBookedDeparture
    }

    fun delayString() : String {
        var delay = delay()
        if (delay==null || delay==0) return ""
        if (delay>0) return "+$delay"
        if (delay>0) return "-$delay"
        return ""
    }

    fun delay() : Int? {

        return if (realtimeDeparture != null && gbttBookedDeparture != null){
            val actual = TimeDate(startTime = realtimeDeparture)
            val booked = TimeDate(startTime = gbttBookedDeparture)
            TimeUnit.MILLISECONDS.toMinutes(booked.calendar.timeInMillis - actual.calendar.timeInMillis).toInt()
        }else null
    }


}