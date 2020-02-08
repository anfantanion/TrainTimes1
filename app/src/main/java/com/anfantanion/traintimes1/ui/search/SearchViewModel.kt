package com.anfantanion.traintimes1.ui.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.repositories.StationRepo

class SearchViewModel() : ViewModel() {

    private var mRepo = StationRepo
    private var mStations : MutableLiveData<List<Station>> = mRepo.getStations()

    val immutableStation:LiveData<List<Station>>
        get() = mStations

}
