package com.anfantanion.traintimes1.models.journeyPlanners

import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.anfantanion.traintimes1.repositories.RTTAPI

class JourneyPlanner(
    var journeyListener: (List<ServiceStub>?) -> (Unit),
    var errorListener: (JourneyPlannerError) -> Unit,
    var allowChangeTimeOf: Int,
    var startTime: String?
) : Response.ErrorListener {

    var serviceStubs = ArrayList<ServiceStub>()
    var services = ArrayList<ServiceResponse>()
    var waypoints = emptyList<StationStub>()
    var index = 0
    var lastTimeDate = TimeDate(startTime=startTime)
    val maxTrainsChecked = 10

    fun plan(waypoints: List<StationStub>) {
        this.waypoints = waypoints
        nextStation()
    }

    fun nextStation(){
        if (index > waypoints.size-2){
            journeyListener(serviceStubs)
            return
        }
        val filter = RTTAPI.Filter(
            from = null,
            to = waypoints[index + 1].crs,
            date = lastTimeDate.getDateTime()
        )
        RTTAPI.requestStation(
            waypoints[index],
            StationResponseListener(),
            this,
            filter
        )
    }

    fun stationResponse(response: StationResponse?){
        if (response?.services == null) return errorListener(JourneyPlannerError(
            type = JourneyPlannerError.ErrorType.NOSERVICEFOUND,
            reason = "Could not find any services between",
            errors = listOf(waypoints[index-1],waypoints[index])))

        index++

        RTTAPI.requestServices(
            serviceStubs = response.services.take(maxTrainsChecked).map{it.toServiceStub()},
            listener = {
                serviceResponseList(it)
            },
            errorListener = this,
            requireStation = waypoints[index]
        )
//        RTTAPI.requestService(
//            interestedService.toServiceStub(),
//            ServiceResponseListener(),
//            this
//        )
    }

    fun serviceResponseList(response: List<ServiceResponse>) {

        val times = response
            .filter { sr -> TimeDate(startTime = sr.getStationArrival(waypoints[index-1])).calendar.timeInMillis > lastTimeDate.calendar.timeInMillis }//Only future
            .sortedBy { sr -> TimeDate(startTime = sr.getStationArrival(waypoints[index])).calendar.timeInMillis } //Sort by time

        if (times.isEmpty()) errorListener(JourneyPlannerError(
            type = JourneyPlannerError.ErrorType.NOSERVICEFOUND,
            reason = "Could not find any services between",
            errors = listOf(waypoints[index-1],waypoints[index])))
        val fastestService = times.first()

        val arrivalTime = fastestService.getStationArrival(waypoints[index]) ?: return errorOut()
        serviceStubs.add(fastestService.toServiceStub())
        services.add(fastestService)
        lastTimeDate.setTime(arrivalTime)
        lastTimeDate.addMinutes(allowChangeTimeOf)
        nextStation()
    }

//    fun serviceResponse(response: ServiceResponse?) {
//        if (response?.locations == null) return errorOut()
//        val arrivalTime = response.getStationArrival(waypoints[index])
//        if (arrivalTime == null){
//            //TODO Splitting trains need more work
//            return errorOut()
//        }
//        lastTimeDate.setTime(arrivalTime)
//        nextStation()
//    }

    private fun errorOut(){
        errorListener(JourneyPlannerError(JourneyPlannerError.ErrorType.OTHER,"Unknown"))
    }

    override fun onErrorResponse(error: VolleyError?) {
        errorListener(JourneyPlannerError(JourneyPlannerError.ErrorType.VOLLEYERROR,"Connection",null,error))
    }

    inner class StationResponseListener :
        Response.Listener<StationResponse> {
        override fun onResponse(response: StationResponse?) {
            stationResponse((response))
        }
    }
//    inner class ServiceResponseListener :
//        Response.Listener<ServiceResponse> {
//        override fun onResponse(response: ServiceResponse?) {
//            serviceResponse(response)
//        }
//    }
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