package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlanner
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerError
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import java.io.Serializable


class ActiveJourney(
    journey: Journey
) : Journey(
    *journey.waypoints,
    givenName = journey.givenName,
    type = journey.type,
    time = journey.time,
    allowChangeTime = journey.allowChangeTime
),
    Serializable {

    companion object {
        private const val serialVersionUID: Long = 1
    }

    public var date: TimeDate? = null

    private var journeyPlan = emptyList<ServiceStub>()

    private fun plan(
        journeyListener: (List<ServiceStub>?) -> (Unit),
        errorListener: (JourneyPlannerError) -> Unit

    ) {
        val x = when (type) {
            Type.DYNAMIC -> JourneyPlanner(journeyListener, errorListener, allowChangeTime, null)
            Type.ARRIVEBY -> JourneyPlanner(journeyListener, errorListener, allowChangeTime, null)
            Type.DEPARTAT -> JourneyPlanner(journeyListener, errorListener, allowChangeTime, time)
        }
        x.plan(waypoints.toList())
    }

    /**
     * Checks if journey is already planned, if so returns that.
     * Returns true if using already calculated journey.
     */
    fun getPlannedServices(
        journeyListener: (List<ServiceStub>?) -> (Unit),
        errorListener: (JourneyPlannerError) -> Unit,
        replanFromService : Int = 0,
        forceTotalReplan: Boolean = false

    ): Boolean {
        if (journeyPlan.isEmpty() || forceTotalReplan) {
            plan(
                {
                    journeyPlan = it ?: journeyPlan
                    date = TimeDate()
                    journeyListener(it)
                },
                {
                    errorListener(it)
                }
            )
            return false
        } else journeyListener(journeyPlan)
        return true
    }

}