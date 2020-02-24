package com.anfantanion.traintimes1.ui.newJourney

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.parcelizable.JourneyStub
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.StationRepo

class NewJourneyViewModel {

    var originalJourney = MutableLiveData<Journey?>()

    var departTime = MutableLiveData<TimeDate>(TimeDate())
    var arriveTime = MutableLiveData<TimeDate>(TimeDate())
    var radioSelection = MutableLiveData<Journey.Type>(Journey.Type.DYNAMIC)
    var journeyTitle = MutableLiveData<String>()
    var journeyTitleChanged = MutableLiveData<Boolean>()


    var stations = MutableLiveData<List<Station>>(emptyList())
    var shouldUpdate = true


    fun setEditingJourney(journeyStub: JourneyStub?){
        val journey =  JourneyRepo.getJourney(journeyStub)?:return
        originalJourney.value = journey
        stations.value = journey.waypoints.toList().mapNotNull{a -> StationRepo.getStation(a)}

        radioSelection.value = journey.type

        when (journey.type){
            Journey.Type.DEPARTAT -> departTime.value = TimeDate(startTime = journey.time)
            Journey.Type.ARRIVEBY -> arriveTime.value = TimeDate(startTime = journey.time)
        }

        journeyTitle.value = journey.givenName
        journeyTitleChanged.value = true
    }

    fun saveJourney() : Boolean {
        val journey = originalJourney.value ?: Journey()
        if (stations.value?.size ?: 0 < 2) return false
        journey.givenName = journeyTitle.value
        journey.waypoints = stations.value?.map{a -> a.toStationStub()}?.toTypedArray() ?: emptyArray()
        radioSelection.value?.let{journey.type = it}
        when (radioSelection.value){
            Journey.Type.DEPARTAT -> journey.time = departTime.value!!.getTime()
            Journey.Type.ARRIVEBY -> journey.time = arriveTime.value!!.getTime()
        }
        JourneyRepo.addJourney(journey)
        return true
    }

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