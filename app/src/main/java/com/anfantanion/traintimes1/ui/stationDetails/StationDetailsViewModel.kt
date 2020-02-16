package com.anfantanion.traintimes1.ui.stationDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.anfantanion.traintimes1.repositories.RTTAPI
import java.util.*

class StationDetailsViewModel : ViewModel(){

    companion object sharedVM{
        var shared : StationDetailsViewModel? = null
    }

    init {
        shared=this
    }

    var station : Station? = null
    var stationResponse = MutableLiveData<StationResponse>()
    var isLoading = MutableLiveData<Boolean>()
    var isError = MutableLiveData<Boolean>()
    var isFiltered = MutableLiveData<Boolean>()
    var lastError : VolleyError? = null

    var stationFilter : Station? = null
    var timeDateFilter : Calendar? = null
    var timeFiltered = false
    var dateFiltered = false



    var filter = emptyMap<String,String>()
    private val maxAge = 0

    fun getServices(){
        val s = station ?: return
        isLoading.value = true
        isError.value = false
        isFiltered.value = filter.isNotEmpty()
        RTTAPI.requestStation(s.code,
            listener = Response.Listener { response ->
                stationResponse.value = response
                isLoading.value = false
            },
            errorListener = Response.ErrorListener { error ->
                lastError = error
                isError.value = true
                isLoading.value = false
            },
            to = filter["to"],
            from = filter["from"],
            date = filter["date"],
            maxAge = maxAge.toLong()
            )
    }

    fun filterToString() : String {
        var stringBuilder = StringBuilder("Filtered -")
        if (filter["to"] != null){
            stringBuilder.append(" To: "+filter["to"])
        }
        if (filter["from"] != null){
            stringBuilder.append(" From: "+filter["from"])
        }
        if (filter["date"] != null){
            stringBuilder.append(" At: "+filter["date"])
        }
        return stringBuilder.toString()
    }


}