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
        private val history = History()

        fun getHistory(count: Int): List<Station.StationSuggestion>{
            return history.getHistory(count)
        }

        fun addHistory(stationSuggestion: Station.StationSuggestion){
            return history.add(stationSuggestion)
        }

        fun getStation(stationSuggestion: Station.StationSuggestion) : Station? {
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
                    val suggestionListCodes = ArrayList<Station.StationSuggestion>()
                    val suggestionListOther = ArrayList<Station.StationSuggestion>()
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
                    results.values = suggestionListCodes.distinct()
                    results.count = suggestionListCodes.size
                    lastSearchTopStationSuggestion = suggestionListCodes[0]
                    return results
                }

                override fun publishResults(
                    constraint: CharSequence,
                    results: FilterResults
                ) {
                    listener?.onResults(results.values as? List<Station.StationSuggestion> ?: emptyList())
                }
            }.filter(query)
        }

        fun findNearby(
            listener: stationSuggestionListener?
        ) {
            //TODO: Location Search

        }

        interface stationSuggestionListener {
            fun onResults(results: List<Station.StationSuggestion>)
        }

        private class History(val size: Int = 3){

            var history = Array<Station.StationSuggestion?>(3) {null}
            private var pointer = 0

            fun getHistory(count: Int = size): List<Station.StationSuggestion>{
                val list = ArrayList<Station.StationSuggestion>()
                for(i in 0 until minOf(size,count)){
                    history[(pointer+i)% size]?.let {list.add(it)}
                }
                return list
            }

            fun add(stationSuggestion: Station.StationSuggestion){
                history[pointer] = stationSuggestion
                pointer = (pointer+1)%3
            }

        }

    }

}