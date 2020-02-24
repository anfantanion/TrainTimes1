package com.anfantanion.traintimes1.ui.savedJourneys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.repositories.JourneyRepo

class SavedJourneysViewModel : ViewModel() {

    val journeys = MutableLiveData<List<Journey>>()
    var doUpdate = true

    fun getJourneys(){
        journeys.value = JourneyRepo.getSavedJourneys()
    }

    fun swapJourneys(start: Int, end: Int){
        JourneyRepo.swapOrder(start,end)
        doUpdate = false
        getJourneys()
    }

}