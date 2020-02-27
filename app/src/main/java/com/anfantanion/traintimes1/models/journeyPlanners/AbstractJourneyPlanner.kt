package com.anfantanion.traintimes1.models.journeyPlanners

import com.anfantanion.traintimes1.models.parcelizable.StationStub

abstract class AbstractJourneyPlanner() {
    abstract fun plan(waypoints: List<StationStub>)
}