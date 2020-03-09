package com.anfantanion.traintimes1.ui.activeJourney

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlannerError
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.RTTAPI
import com.anfantanion.traintimes1.repositories.StationRepo

class ActiveJourneyViewModel : ViewModel() {

    var activeJourney = MutableLiveData<ActiveJourney?>(null)

    var serviceResponses = MutableLiveData<List<ServiceResponse>>()

    var loadedPreviouslyPlannedRoute = MutableLiveData<Boolean>(false)

    var isLoading = MutableLiveData<Boolean>(false)
    var isError = MutableLiveData<Boolean>(false)
    var lastError = VolleyError()
    var errorText = MutableLiveData<String>()

    var refreshAge = MutableLiveData<Int?>(null)

    init {
        val temp = JourneyRepo.activeJourney.value
        if (temp!=null){
            if (temp.dateOfPlan?.getDate() != null && temp.dateOfPlan?.getDate() != TimeDate().getDate()){
                JourneyRepo.activeJourney.value = null
                errorText.value = "Active Journey has Expired"
            }else {
                activeJourney.value = temp
            }
        }
    }


    fun getServices(
        replanFrom: Int = -1,
        _forceReplan: Boolean = false
    ){
        var forceReplan = _forceReplan
        val journey = activeJourney.value
        if (journey == null) {
            isError.value = true
            return
        }
        isLoading.value = true

        //If only part replanning, supply static services.
        var initialServices: ArrayList<ServiceResponse>? = null
        val serviceResponses2 = serviceResponses.value
        if( replanFrom > 0 && serviceResponses2 != null){
            forceReplan = true
            initialServices = ArrayList()
            for (i in 0 until replanFrom){
                initialServices.add(serviceResponses2[i])
            }
        }
        if (replanFrom == 0 || _forceReplan){
            activeJourney.value!!.time = TimeDate().getTime()
            forceReplan = true
        }

        loadedPreviouslyPlannedRoute.value = journey.getPlannedServices({onPlanned(it)},{onError(it)},initialServices,forceReplan)
    }


    fun getWaypointStations(): List<Station>?{
        if (activeJourney.value == null) return null
        return activeJourney.value!!.waypoints.mapNotNull{sS -> StationRepo.getStation(sS) }
    }


    fun getCurrentServiceNo() : Int?{
        return activeJourney.value?.getCurrentServiceNo()
    }

    private fun onPlanned(serviceStubs : List<ServiceStub>?){
        if (serviceStubs == null){
            isError.value = true
            return
        }
        activeJourney.value!!.getServiceResponses(
            listener = {
                serviceResponses.value = it
                isLoading.value = false
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
            JourneyPlannerError.ErrorType.NOSERVICEFOUND -> {journeyPlannerError.reason}
            JourneyPlannerError.ErrorType.OTHER -> journeyPlannerError.reason ?: "Unknown"
        }
        //activeJourney.value = null
        //errorText.value = journeyPlannerError.reason

    }

    fun getNextChange() : ActiveJourney.Change? {
        return activeJourney.value?.getNextChange()
    }



}