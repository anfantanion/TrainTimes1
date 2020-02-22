package com.anfantanion.traintimes1.models.journeyPlanners

import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.anfantanion.traintimes1.repositories.RTTAPI

class JourneyPlanner : Response.ErrorListener
{
    var journeyListener: JourneyListener? = null
    var services = ArrayList<ServiceStub>()
    var waypoints = emptyList<StationStub>()
    var index = 0
    var timeDate = TimeDate()
    val leeway = 0

    fun plan(waypoints: List<StationStub>) {
        this.waypoints = waypoints
        nextStation()
    }

    fun nextStation(){
        if (index > waypoints.size-2){
            journeyListener?.journeyPlanned(services)
            return
        }
        val filter = RTTAPI.Filter(
            from = null,
            to = waypoints[index + 1].crs,
            date = timeDate.getDateTime()
        )
        RTTAPI.requestStation(
            waypoints[index],
            StationResponseListener(),
            this,
            filter
        )
    }

    fun stationResponse(response: StationResponse?){
        index++
        if (response?.services == null) return errorOut()
        val interestedService = response.nextService(timeDate) ?: return errorOut()
        services.add(interestedService.toServiceStub())
        RTTAPI.requestService(
            interestedService.toServiceStub(),
            ServiceResponseListener(),
            this
        )
    }

    fun serviceResponse(response: ServiceResponse?) {
        if (response?.locations == null) return errorOut()
        val arrivalTime = response.getStationArrival(waypoints[index])!!
        timeDate.setTime(arrivalTime)
        nextStation()
    }

    private fun errorOut(){
        journeyListener?.journeyPlanned(null)
    }

    override fun onErrorResponse(error: VolleyError?) {
        var x = 0
    }

    interface JourneyListener{
        fun journeyPlanned(journey : List<ServiceStub>?)
    }

    inner class StationResponseListener :
        Response.Listener<StationResponse> {
        override fun onResponse(response: StationResponse?) {
            stationResponse((response))
        }
    }
    inner class ServiceResponseListener :
        Response.Listener<ServiceResponse> {
        override fun onResponse(response: ServiceResponse?) {
            serviceResponse(response)
        }
    }
}

//    class PlannerDynamic() : JourneyPlanner() {
//
//        override fun plan(waypoints: List<StationStub>): List<ServiceStub> {
//            RTTAPI.requestStation(waypoints[index],this,this)
//        }
//
//        override fun onResponse(response: StationResponse?) {
//            if (response == null) journeyListener?.journeyPlanned(null)
//            var interestedService = response?.services?.get(0)
//
//
//        }
//
//        override fun onErrorResponse(error: VolleyError?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }

//    class PlannerDepartAt(val time: String, val date: String?=null) : JourneyPlanner() {
//        override fun plan(waypoints: List<StationStub>): List<ServiceStub> {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }
//
//    class PlannerArriveBy(val time: String, val date: String?=null) : JourneyPlanner() {
//        override fun plan(waypoints: List<StationStub>): List<ServiceStub> {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }