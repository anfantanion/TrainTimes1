package com.anfantanion.traintimes1.repositories

import android.content.Context
import android.widget.Filter
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.parcelizable.StationStub

object StationRepo {


    private var mutableStations = MutableLiveData<List<Station>>()
    private var stations = ArrayList<Station>()
    private var stationCodeLookup = HashMap<String, Station>()



    private lateinit var context: Context
    private var rttAPI = RTTAPI
    lateinit var volleyRequestQueue: RequestQueue


    fun setContext(context: Context) {
        this.context = context
        volleyRequestQueue = Volley.newRequestQueue(context)
        rttAPI.setContext(context)
    }

    fun getStations(): MutableLiveData<List<Station>> {
        if (stations.isNullOrEmpty()) {
            loadStations()
        }
        mutableStations.value = stations
        return mutableStations
    }

    fun getStation(stationStub: StationStub?): Station? {
        if (stationStub == null) return null
        return stationCodeLookup[stationStub.crs]
    }

    fun loadStations(){
        context.resources.openRawResource(R.raw.station_codes_geo_final_rad_android).bufferedReader().useLines {lines ->
            for (line in lines) {
                val linesplit = line.split(":")
                val station = Station(linesplit[0],linesplit[1],linesplit[2].toDouble(),linesplit[3].toDouble())
                stations.add(station)
                stationCodeLookup[station.code] = station
            }
        }
        mutableStations.value = stations
    }


    object SearchManager{

        private var recentStations = ArrayList<Station>()
        var lastSearchTopStationSuggestion : Station.StationSuggestion? = null

        interface stationSuggestionListener {
            fun onResults(results: List<Station.StationSuggestion>)
        }

        fun getHistory(count: Int): List<Station.StationSuggestion>{
            var history = ArrayList<Station.StationSuggestion>()
            var i = 0
            for (s: Station in recentStations){
                history.add(s.getStationSuggestion())
                i++
                if (i>=count) break
            }
            history.add(stations[0].getStationSuggestion())
            history.add(stations[1].getStationSuggestion())
            return history
        }

        fun getStation(stationSuggestion: Station.StationSuggestion) : Station? {
            var x = stations.filter {
                it.code == "AXM"
            }
            return stationCodeLookup[stationSuggestion.code]
        }

        fun findSuggestions(
            query: String,
            limit: Int,
            listener: stationSuggestionListener?
        ) {
            object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    //TODO: Need a much better sorting algorithm
                    // This one introduces duplicates due to the two lists.
                    val suggestionListCodes: MutableList<Station.StationSuggestion> = ArrayList()
                    val suggestionListOther: MutableList<Station.StationSuggestion> = ArrayList()
                    if (!(constraint.isEmpty())) {
                        for (station in stations) {
                            if (station.code.startsWith(constraint, ignoreCase = true)){
                                suggestionListCodes.add(station.getStationSuggestion())
                            }
                            if (station.name.contains(constraint, ignoreCase = true)){
                                suggestionListOther.add(station.getStationSuggestion())
                            }
                        }
                    }
                    val results = FilterResults()
                    suggestionListCodes.sortBy{it.code}
                    suggestionListOther.sortBy{it.name}
                    suggestionListCodes.addAll(suggestionListOther)
                    results.values = suggestionListCodes
                    results.count = suggestionListCodes.size
                    lastSearchTopStationSuggestion = suggestionListCodes[0]
                    return results
                }

                override fun publishResults(
                    constraint: CharSequence,
                    results: FilterResults
                ) {
                    if (listener != null) {
                        listener.onResults(results.values as List<Station.StationSuggestion>)
                    }
                }
            }.filter(query)
        }

        fun findNearby(
            listener: stationSuggestionListener?
        ) {
            //TODO: Location Search

        }

    }

}