package com.anfantanion.traintimes1.ui.activeJourney

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.RTTAPI

class ActiveJourneyViewModel : ViewModel() {

    var activeJourney = MutableLiveData<Journey>(JourneyRepo.activeJourney)

    var serviceResponses = MutableLiveData<List<ServiceResponse>>()

    var isLoading = MutableLiveData<Boolean>(false)
    var isError = MutableLiveData<Boolean>(false)
    var lastError = VolleyError()

    fun getServices(){
        val journey = activeJourney.value
        if (journey == null) {
            isError.value = true
            return
        }
        isLoading.value = true
        journey.getPlannedServices {onPlanned(it)}
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
            })

    }

}