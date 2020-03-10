package com.anfantanion.traintimes1.models

import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlanner
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerError
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerReverse
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.RTTAPI
import java.io.Serializable
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


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

    var dateOfPlan: TimeDate? = null

    @Transient var lastRefresh = System.currentTimeMillis()
    @Transient var changing = false
    @Transient var changingLock = ReentrantLock()

    var journeyPlan = emptyList<ServiceStub>()
    @Transient var journeyPlanResponse : List<ServiceResponse>? = null

    private fun plan(
        journeyListener: (List<ServiceStub>?) -> (Unit),
        errorListener: (JourneyPlannerError) -> Unit,
        initialServices : List<ServiceResponse>? = null

    ) {
        changing = true
        val listener = {it: List<ServiceStub>? ->
            changing = false
            journeyListener(it)
        }
        val error = { it: JourneyPlannerError ->
            changing = false
            errorListener(it)
        }
        val x = when (type) {
            Type.DYNAMIC -> JourneyPlanner(listener, error, allowChangeTime, null).plan(waypoints.toList(),initialServices)
            Type.ARRIVEBY -> JourneyPlannerReverse(listener, error, allowChangeTime, null).plan(waypoints.toList(),initialServices)
            Type.DEPARTAT -> JourneyPlanner(listener, error, allowChangeTime, time).plan(waypoints.toList(),initialServices)
        }
        x
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

    fun getServiceResponses(
        listener: (List<ServiceResponse>) -> (Unit),
        errorListener: Response.ErrorListener?
    ){
        changing = true
        RTTAPI.requestServices(
            journeyPlan,
            listener = {
                //Sort the result
                val x = Array<ServiceResponse?>(journeyPlan.size){null}
                if (it.isEmpty()) return@requestServices
                it.forEach {sr ->
                    x[journeyPlan.indexOf(sr.toServiceStub())] = sr
                }
                val y = x.toList().filterNotNull()
                lastRefresh = System.currentTimeMillis()
                journeyPlanResponse = y
                listener(y)
            },
            errorListener = errorListener

        )

    }

    fun getCurrentServiceNo() : Int?{
        return journeyPlanResponse?.indexOf(getCurrentService())
    }

    fun getCurrentService() : ServiceResponse? {
        val aj = this
        val serviceResponses = journeyPlanResponse ?: return null

        for (i in serviceResponses.indices){
            val x = serviceResponses.filter{ sr ->
                TimeDate(startTime = sr.getRTStationDeparture(aj.waypoints[i])).calendar.timeInMillis < TimeDate().calendar.timeInMillis &&
                        TimeDate(startTime = sr.getRTStationDeparture(aj.waypoints[i+1])).calendar.timeInMillis > TimeDate().calendar.timeInMillis
            }
            if (x.isNotEmpty()) return x.first()
        }
        return serviceResponses.getOrNull(0)
    }

    fun getNextChange() : Change? {
        val x = getCurrentServiceNo() ?: return null
        return getChanges()?.getOrNull(x)
    }

    fun getChanges(): List<Change>?{

        val journeyPlanLocal = journeyPlanResponse ?: return null

        if (journeyPlanLocal.size <= 1) return null
        if (journeyPlanLocal.size == 1) return null

        val changes = ArrayList<Change>()
        for (i in 0..journeyPlan.size-2){
            val c = Change(
                journeyPlanLocal[i],
                journeyPlanLocal[i+1],
                this.waypoints[i+1]
            )
            changes.add(c)
        }
        return changes
    }

    class Change(
        val service1 : ServiceResponse,
        val service2 : ServiceResponse,
        val waypoint : StationStub
    ){
        fun arrivalTime() : String?{
            return service1.getRTStationArrival(waypoint)
        }

        fun departureTime() : String?{
            return service2.getRTStationDeparture(waypoint)
        }


    }



}