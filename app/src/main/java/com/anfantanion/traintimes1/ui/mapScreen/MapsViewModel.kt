package com.anfantanion.traintimes1.ui.mapScreen

import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.RTTAPI

class MapsViewModel(var serviceStubs :  Array<ServiceStub>){

    val serviceResponses = MutableLiveData<Array<ServiceResponse?>>(Array(serviceStubs.size){null})

    var isLoading = MutableLiveData<Boolean>()
    var isError = MutableLiveData<Boolean>()
    var lastError : VolleyError? = null

    fun getServiceDetails(){
        isLoading.value = true
        isError.value = false
        for (pos in serviceStubs.indices){
            getServiceDetailsSingle(pos)
        }
    }

    private fun getServiceDetailsSingle(pos: Int){

        RTTAPI.requestService(serviceStubs[pos],
            listener = Response.Listener { response ->
                serviceResponses.value?.set(pos, response)
                serviceResponses.value = serviceResponses.value;
                isLoading.value = false
            },
            errorListener = Response.ErrorListener { error ->
                lastError = error
            })
    }

}