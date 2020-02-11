package com.anfantanion.traintimes1.ui.stationDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.stationResponse.Service
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.anfantanion.traintimes1.repositories.RTTAPI

class StationDetailsViewModel( var station : Station? = null) : ViewModel(){

    var stationResponse = MutableLiveData<StationResponse>()
    var isLoading = MutableLiveData<Boolean>()
    var isError = MutableLiveData<Boolean>()
    var lastError : VolleyError? = null





    fun getServices(){
        val s = station;
        if (s==null)return
        isLoading.value = true
        RTTAPI.requestStation(s.code,
            listener = Response.Listener { response ->
                stationResponse.value = response
                isLoading.value = false
            },
            errorListener = Response.ErrorListener { error ->
                isError.value = true
                isLoading.value = false
                lastError = error
            }

            )
    }


}