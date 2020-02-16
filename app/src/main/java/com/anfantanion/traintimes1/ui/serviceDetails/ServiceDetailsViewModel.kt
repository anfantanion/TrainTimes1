package com.anfantanion.traintimes1.ui.serviceDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.RTTAPI

class ServiceDetailsViewModel() : ViewModel() {

    lateinit var service : ServiceStub

    var serviceResponse = MutableLiveData<ServiceResponse>()
    var isLoading = MutableLiveData<Boolean>()
    var isError = MutableLiveData<Boolean>()
    var lastError : VolleyError? = null

    fun getServiceDetails(){
        isLoading.value = true
        isError.value = false
        RTTAPI.requestService(service,
            listener = Response.Listener { response ->
                serviceResponse.value = response
                isLoading.value = false
            },
            errorListener = Response.ErrorListener { error ->
                lastError = error
                isError.value = true
                isLoading.value = false
            })
    }

}