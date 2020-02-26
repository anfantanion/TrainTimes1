package com.anfantanion.traintimes1.models.stationResponse

data class Destination(
    val description: String,
    val publicTime: String,
    val tiploc: String,
    val workingTime: String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Destination

        if (tiploc != other.tiploc) return false

        return true
    }

    override fun hashCode(): Int {
        return tiploc.hashCode()
    }
}

