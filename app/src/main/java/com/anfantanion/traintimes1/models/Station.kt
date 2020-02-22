package com.anfantanion.traintimes1.models

import android.os.Parcel
import android.os.Parcelable
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
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
    val longitudeRAD : Double = 0.0) {
    private val earthRadiusKM = 3671


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

    fun getStationSuggestion() : StationSuggestion {
        return StationSuggestion(name,code)
    }

    fun toStationStub(): StationStub {
        return StationStub(code)
    }


    class StationSuggestion(val name: String,
                            val code: String) : SearchSuggestion {


        constructor(parcel: Parcel) : this(
            parcel.readString()?:"",
            parcel.readString()?:""
        )

        override fun getBody(): String {
            return "$name ($code)"
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(code)
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StationSuggestion

            if (code != other.code) return false

            return true
        }

        override fun hashCode(): Int {
            return code.hashCode()
        }

        companion object CREATOR : Parcelable.Creator<StationSuggestion> {
            override fun createFromParcel(parcel: Parcel): StationSuggestion {
                return StationSuggestion(parcel)
            }

            override fun newArray(size: Int): Array<StationSuggestion?> {
                return arrayOfNulls(size)
            }
        }


    }




}
