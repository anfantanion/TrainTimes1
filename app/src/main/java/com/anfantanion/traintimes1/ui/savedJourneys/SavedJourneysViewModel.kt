package com.anfantanion.traintimes1.ui.savedJourneys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.repositories.JourneyRepo

class SavedJourneysViewModel : ViewModel() {

    val journeys = MutableLiveData<List<Journey>>()

    val editMode = MutableLiveData<Boolean>(false)

    var doUpdate = true

    fun getJourneys(){
        journeys.value = JourneyRepo.getSavedJourneys()
    }

    fun copyJourney(journey: Journey) : Journey{
        val newJourney = journey.getCopy()
        assert(newJourney.uuid!= journey.uuid)
        JourneyRepo.addJourney(newJourney)
        getJourneys()
        return newJourney
    }

    fun removeJourney(journey: Journey){
        JourneyRepo.removeJourney(journey)
        getJourneys()
    }

    fun swapJourneys(start: Int, end: Int){
        JourneyRepo.swapOrder(start,end)
        doUpdate = false
        getJourneys()
    }

    fun toggleEdit(): Boolean{
        editMode.value = !editMode.value!!
        return editMode.value!!
    }

}