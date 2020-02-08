package com.anfantanion.traintimes1.models.stationResponse

data class LocationDetail(
    val crs: String,
    val description: String,
    val destination: List<Destination>,
    val displayAs: String,
    val gbttBookedArrival: String,
    val gbttBookedDeparture: String,
    val isCall: Boolean,
    val isPublicCall: Boolean,
    val line: String?,
    val lineConfirmed: Boolean?,
    val origin: List<Origin>,
    val path: String?,
    val pathConfirmed: Boolean?,
    val platform: String,
    val platformChanged: Boolean,
    val platformConfirmed: Boolean,
    val realtimeActivated: Boolean,
    val realtimeArrival: String,
    val realtimeArrivalActual: Boolean,
    val realtimeDeparture: String,
    val realtimeDepartureActual: Boolean,
    val tiploc: String
)