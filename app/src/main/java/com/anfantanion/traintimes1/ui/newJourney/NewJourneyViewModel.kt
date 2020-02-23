package com.anfantanion.traintimes1.ui.newJourney

import androidx.lifecycle.MutableLiveData
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.TimeDate
import java.util.*

class NewJourneyViewModel {

    var departTime = MutableLiveData<TimeDate>(TimeDate())
    var arriveTime = MutableLiveData<TimeDate>(TimeDate())

    var stations = MutableLiveData<List<Station>>(emptyList())
    var shouldUpdate = true

    fun swapStations(start: Int, end: Int){
        val x = stations.value?.toMutableList() ?: return
        x[start] = x[end].also {x[end] = x[start]}
        stations.value = x
    }

    fun addStation(station: Station){
        val x = stations.value?.toMutableList() ?: return
        x.add(station)
        stations.value = x
    }

    fun replaceStation(position: Int, station: Station) {
        val x = stations.value?.toMutableList() ?: return
        x[position] = (station)
        stations.value = x
    }

    fun removeStation(position: Int) {
        val x = stations.value?.toMutableList() ?: return
        x.removeAt(position)
        stations.value = x
    }

    fun reverseStations(){
        val x = stations.value?.toMutableList() ?: return
        x.reverse()
        stations.value = x
    }

}