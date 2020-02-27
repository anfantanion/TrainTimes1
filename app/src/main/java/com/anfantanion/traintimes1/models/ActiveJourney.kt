package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlanner
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import java.io.Serializable


class ActiveJourney(
    journey: Journey
) : Journey(
    *journey.waypoints,
    givenName = journey.givenName,
    type = journey.type,
    time = journey.time
),
    Serializable {

    companion object {
        private const val serialVersionUID: Long = 1
    }

    private var journeyPlan = emptyList<ServiceStub>()

    private fun plan(journeyListener: (List<ServiceStub>?) -> (Unit)) {
        val x = when (type) {
            Type.DYNAMIC -> JourneyPlanner(journeyListener)
            Type.ARRIVEBY -> JourneyPlanner(journeyListener)
            Type.DEPARTAT -> JourneyPlanner(journeyListener)
        }
        x.plan(waypoints.toList())
    }

    /**
     * Checks if journey is already planned, if so returns that.
     */
    fun getPlannedServices(journeyListener: (List<ServiceStub>?) -> (Unit)) {
        if (journeyPlan.isEmpty()) plan {
            journeyPlan = it ?: journeyPlan
            journeyListener(it)
        }
        else journeyListener(journeyPlan)
    }

}