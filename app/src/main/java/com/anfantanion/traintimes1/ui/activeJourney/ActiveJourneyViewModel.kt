package com.anfantanion.traintimes1.ui.activeJourney

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerError
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.stationResponse.Service
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.RTTAPI
import com.anfantanion.traintimes1.repositories.StationRepo

class ActiveJourneyViewModel : ViewModel() {

    var activeJourney = MutableLiveData<ActiveJourney>(JourneyRepo.activeJourney)

    var serviceResponses = MutableLiveData<List<ServiceResponse>>()

    var loadedPreviouslyPlannedRoute = MutableLiveData<Boolean>(false)

    var isLoading = MutableLiveData<Boolean>(false)
    var isError = MutableLiveData<Boolean>(false)
    var lastError = VolleyError()
    var errorText = MutableLiveData<String>()

    fun getServices(){
        val journey = activeJourney.value
        if (journey == null) {
            isError.value = true
            return
        }
        isLoading.value = true
        loadedPreviouslyPlannedRoute.value = journey.getPlannedServices({onPlanned(it)},{onError(it)})
    }

    fun getWaypointStations(): List<Station>?{
        if (activeJourney.value == null) return null
        return activeJourney.value!!.waypoints.mapNotNull{sS -> StationRepo.getStation(sS) }
    }


    fun getCurrentService() : ServiceResponse {
        val aj = activeJourney.value!!

        for (i in serviceResponses.value!!.indices){
            val x = serviceResponses.value!!.filter{ sr ->
                TimeDate(startTime = sr.getRTStationDeparture(aj.waypoints[i])).calendar.timeInMillis < TimeDate().calendar.timeInMillis &&
                        TimeDate(startTime = sr.getRTStationDeparture(aj.waypoints[i+1])).calendar.timeInMillis > TimeDate().calendar.timeInMillis
            }
            if (x.isNotEmpty()) return x.first()
        }
        return serviceResponses.value!![0]

    }

    private fun onPlanned(serviceStubs : List<ServiceStub>?){
        if (serviceStubs == null){
            isError.value = true
            return
        }
        RTTAPI.requestServices(
            serviceStubs,
            listener = {
                serviceResponses.value = it
            },
            errorListener = Response.ErrorListener { error ->
                lastError = error
                isError.value = true
                isLoading.value = false
            }

        )

    }

    private fun onError(journeyPlannerError: JourneyPlannerError){
        isError.value = true
        isLoading.value = false
        errorText.value = when (journeyPlannerError.type){
            JourneyPlannerError.ErrorType.VOLLEYERROR -> {journeyPlannerError.volleyError?.localizedMessage}
            JourneyPlannerError.ErrorType.NOSERVICEFOUND -> {journeyPlannerError.reason+journeyPlannerError.errors?.toString()}
            JourneyPlannerError.ErrorType.OTHER -> "Unknown"
        }
        errorText.value = journeyPlannerError.reason

    }

}