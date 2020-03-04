package com.anfantanion.traintimes1.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.repositories.JourneyRepo

class HomeViewModel : ViewModel() {

    var favouriteJourneys = MutableLiveData<List<Journey>>(emptyList())
    var activeJourney = MutableLiveData<ActiveJourney?>(null)

    fun getFavourites(){
        activeJourney.value = JourneyRepo.activeJourney
        favouriteJourneys.value = JourneyRepo.getFavourites()

    }
}