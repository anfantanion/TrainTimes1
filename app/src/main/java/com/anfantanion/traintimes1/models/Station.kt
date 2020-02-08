package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.stationResponse.Location
import com.anfantanion.traintimes1.models.stationResponse.StationRepsponse
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * latitude and longitude is in radians
 */
class Station(
    val name: String,
    val code: String,
    val latitudeRAD : Double = 0.0,
    val longitudeRAD : Double = 0.0)
{
    private val earthRadiusKM = 3671
    private var cachedStationRepsponse = null


    /**
     * Haversine formula as per
     * https://www.movable-type.co.uk/scripts/latlong.html
     */
    fun distanceToKM(latitudeRAD2: Double, longitudeRAD2: Double) : Double{
        val dLat = latitudeRAD2-latitudeRAD
        val dLon = longitudeRAD2-longitudeRAD
        val a = sin(dLat/2) * sin(dLat/2) +
                sin(dLon/2) * sin(dLon/2) * cos(longitudeRAD) * cos(latitudeRAD)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        return earthRadiusKM * c
    }

    fun distanceToKM(station: Station):Double{
        return distanceToKM(station.latitudeRAD,station.longitudeRAD)
    }

    fun getStationInfo(forceRefresh: Boolean = false) : StationRepsponse{
        return StationRepsponse(null, Location("1","2","3"), emptyList())
    }



}
