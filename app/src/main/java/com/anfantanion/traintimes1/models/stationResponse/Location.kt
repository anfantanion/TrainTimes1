package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.repositories.SingleObjectToListFactory
import com.google.gson.annotations.JsonAdapter

data class Location(
    val crs: String,
    val name: String,
    @JsonAdapter(SingleObjectToListFactory::class)
    val tiploc: List<String>
)