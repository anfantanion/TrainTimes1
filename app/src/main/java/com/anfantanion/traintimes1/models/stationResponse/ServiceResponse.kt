package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.destinationName
import com.anfantanion.traintimes1.models.differenceOfTimesMinutes
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub

data class ServiceResponse(
    val atocCode: String,
    val atocName: String,
    val isPassenger: Boolean,
    var locations: List<LocationDetail>?,
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

    fun age(): Int{
        return differenceOfTimesMinutes(timestamp,System.currentTimeMillis())
    }

    fun getName(): String{
        return origin[0].publicTime +" to " + destinationName(destination)
    }

    fun getName(stationStub : StationStub): String{
        val locationDetail = filterLocations(stationStub)?.firstOrNull() ?: return getName()
        return locationDetail.getDepartureTime() +" to " + destinationName(destination)
    }

    fun getStationArrival(stationStub: StationStub) : String? {
        var x = filterLocations(stationStub)
        if (x==null || x.isEmpty()) return null
        return x?.get(0)?.gbttBookedArrival
    }

    fun getPlatform(stationStub: StationStub) : String? {
        return filterLocations(stationStub)?.getOrNull(0)?.platform
    }

    fun getMostRecentLocation() : LocationDetail?{
        return locations?.lastOrNull {
            TimeDate(startTime = it.getDepartureTime() ?: it.getArrivalTime()).calendar.timeInMillis < TimeDate().calendar.timeInMillis
        }

    }

    fun getPositionOfStation(stationStub: StationStub) : Int? {
        return locations?.indexOfFirst{ ld -> ld.crs == stationStub.crs }
    }


    fun getRTorTTArrival(stationStub: StationStub) : String? {
        return filterLocations(stationStub)?.getOrNull(0)?.getArrivalTime()
    }

    fun getRTorTTDeparture(stationStub: StationStub) : String? {
        return filterLocations(stationStub)?.getOrNull(0)?.getDepartureTime()
    }

    fun getRTStationArrival(stationStub: StationStub) : String? {
        return filterLocations(stationStub)?.getOrNull(0)?.realtimeArrival
    }

    fun getStationDeparture(stationStub: StationStub) : String? {
        return filterLocations(stationStub)?.getOrNull(0)?.gbttBookedDeparture
    }

    fun getRTStationDeparture(stationStub: StationStub) : String? {
        return filterLocations(stationStub)?.getOrNull(0)?.realtimeDeparture
    }

    fun toServiceStub() : ServiceStub{
        return ServiceStub(serviceUid,runDate)
    }

    fun getTimeOnTrain(start: StationStub, end: StationStub): Int{
        return differenceOfTimesMinutes(getStationDeparture(start)!!,getStationArrival(end)!!)
    }

    fun filterLocations(stationStub: StationStub): List<LocationDetail>?{
        return locations?.filter{ it.crs == stationStub.crs }
    }

}