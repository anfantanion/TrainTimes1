package com.anfantanion.traintimes1.models

import com.android.volley.Response
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlanner
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerError
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerReverse
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.notify.NotifyManager
import com.anfantanion.traintimes1.repositories.RTTAPI
import java.io.Serializable
import java.security.Key
import java.util.concurrent.locks.ReentrantLock


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
                NotifyManager.setNextNotification(this)
                if (!isExpired()){
                    NotifyManager.queueNextRefresh(this)
                }
                listener(y)
            },
            errorListener = errorListener

        )

    }

    fun isExpired(): Boolean {
        val last = getKeypoints2()?.last() ?: return true
        val time = last.service1.getRTorTTArrival(last.waypoint!!) ?: return false
        return (stringTimeToMilis(time) < System.currentTimeMillis())
    }

    fun getCurrentServiceNo() : Int?{
        return null
    }

    fun getKeypoints2() : List<KeyPoint>? {
        val aj = this
        val serviceResponses = journeyPlanResponse ?: return null
        val currentTime = TimeDate().calendar.timeInMillis
        var x = ArrayList<KeyPoint>()
        var sr = serviceResponses[0] //Before Journey
        x.add(
            KeyPoint(
                sr,
                null,
                0,
                stringTimeToMilis(sr.getRTStationDeparture(aj.waypoints[0])!!),
                waypoints[0],
                KeyPoint.ChangeType.START
            )
        )

        for (i in 0..serviceResponses.size-2) { // For each service
            sr = serviceResponses[i]
            val station1Dep =
                TimeDate(startTime = sr.getRTStationDeparture(aj.waypoints[i])).calendar.timeInMillis
            val station2Dep =
                TimeDate(startTime = sr.getRTStationDeparture(aj.waypoints[i + 1])).calendar.timeInMillis

            if (i < serviceResponses.size - 1) { // If not Last Service
                val service2Dep =
                    TimeDate(startTime = serviceResponses[i + 1].getRTStationDeparture(aj.waypoints[i + 1])).calendar.timeInMillis
                x.add(
                    KeyPoint(
                        sr,
                        serviceResponses[i + 1],
                        station1Dep,
                        service2Dep,
                        waypoints[i + 1],
                        KeyPoint.ChangeType.CHANGE
                    )
                )
            }
        }

        sr = serviceResponses.last()
        x.add(
            KeyPoint(
                sr,
                null,
                stringTimeToMilis(sr.getRTStationArrival(aj.waypoints[serviceResponses.size])!!),
                Long.MAX_VALUE,
                waypoints.last(),
                KeyPoint.ChangeType.END
            )
        )

        return x
    }

    fun getCurrentKeyPoint() : KeyPoint? {
        var x = getKeypoints2()
        x = x?.filter { kp ->
            kp.validFrom < System.currentTimeMillis() && kp.validTo > System.currentTimeMillis() || kp.changeType==KeyPoint.ChangeType.END
        }
        return x?.firstOrNull()
    }

    fun getNextChange() : KeyPoint? {
        var x = getKeypoints2()?.filter {kp ->
            kp.changeType==KeyPoint.ChangeType.CHANGE && kp.validTo > System.currentTimeMillis()
        }
        return x?.firstOrNull()
    }

    class KeyPoint(
        val service1: ServiceResponse,
        val service2: ServiceResponse?,
        val validFrom: Long,
        val validTo:Long,
        val waypoint: StationStub?,
        val changeType : ChangeType

    ) {
        fun arrivalTime(): String? {
            return service1.getRTStationArrival(waypoint!!)
        }

        fun departureTime(): String? {
            return service2?.getRTStationDeparture(waypoint!!)
        }

        enum class ChangeType {
            START,
            CHANGE,
            END,
            SINGLE
        }
    }



}