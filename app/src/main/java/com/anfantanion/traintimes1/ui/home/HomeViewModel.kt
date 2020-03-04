package com.anfantanion.traintimes1.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.repositories.JourneyRepo

class HomeViewModel : ViewModel() {

    var favouriteJourneys = MutableLiveData<List<Journey>>(emptyList())
    var activeJourney = MutableLiveData<ActiveJourney?>(null)

    val observer = Observer<ActiveJourney?>{
        activeJourney.value = it
    }

    init {
        JourneyRepo.activeJourney.observeForever(observer)
    }

    fun getFavourites(){
        activeJourney.value = JourneyRepo.activeJourney.value
        favouriteJourneys.value = JourneyRepo.getFavourites()

        activeJourney.value?.getServiceResponses(
            listener = {
                activeJourney.value = activeJourney.value
            },
            errorListener = Response.ErrorListener { error ->

            }
        )

    }

    override fun onCleared() {
        JourneyRepo.activeJourney.removeObserver(observer)
        super.onCleared()
    }
}