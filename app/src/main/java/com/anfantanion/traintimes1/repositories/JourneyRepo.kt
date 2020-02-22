package com.anfantanion.traintimes1.repositories

import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.models.parcelizable.StationStub

object JourneyRepo {

    fun getSavedJourneys(): List<Journey>{
        return listOf(
            Journey(StationStub("AXM"),StationStub("SAL"),StationStub("SOU"),journeyPlanner = Journey.JourneyPlanner()),
            Journey(StationStub("AXM"),StationStub("WAT"),journeyPlanner = Journey.JourneyPlanner())
        )
    }
}