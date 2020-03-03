package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlanner
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerError
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
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

    public var dateOfPlan: TimeDate? = null

    private var journeyPlan = emptyList<ServiceStub>()

    private fun plan(
        journeyListener: (List<ServiceStub>?) -> (Unit),
        errorListener: (JourneyPlannerError) -> Unit,
        initialServices : List<ServiceResponse>? = null

    ) {
        val x = when (type) {
            Type.DYNAMIC -> JourneyPlanner(journeyListener, errorListener, allowChangeTime, null)
            Type.ARRIVEBY -> JourneyPlanner(journeyListener, errorListener, allowChangeTime, null)
            Type.DEPARTAT -> JourneyPlanner(journeyListener, errorListener, allowChangeTime, time)
        }
        x.plan(waypoints.toList(),initialServices)
    }

    /**
     * Checks if journey is already planned, if so returns that.
     * Returns true if using already calculated journey.
     * @param initialServices To perform a partial replan, supply services to base off. (Implies forceTotalReplan)
     * @param forceTotalReplan Force the plan to be recalculated rather than recalled from cache.
     */
    fun getPlannedServices(
        journeyListener: (List<ServiceStub>?) -> (Unit),
        errorListener: (JourneyPlannerError) -> Unit,
        initialServices : List<ServiceResponse>? = null,
        forceTotalReplan: Boolean = false

    ): Boolean {
        if (journeyPlan.isEmpty() || forceTotalReplan || initialServices!=null) {
            plan(
                {
                    journeyPlan = it ?: journeyPlan
                    dateOfPlan = TimeDate()
                    journeyListener(it)
                },
                {
                    errorListener(it)
                },
                initialServices
            )
            return false
        } else journeyListener(journeyPlan)
        return true
    }

}