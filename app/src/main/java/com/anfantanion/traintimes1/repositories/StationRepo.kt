package com.anfantanion.traintimes1.repositories

import android.content.Context
import android.os.AsyncTask
import android.widget.Filter
import androidx.lifecycle.MutableLiveData
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.beust.klaxon.Klaxon
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object StationRepo {


    private var mutableStations = MutableLiveData<List<Station>>()
    private var stations = ArrayList<Station>()

    private var stationCodeLookup = HashMap<String, Station>()




    lateinit var context: Context

//        listOf(
//        Station("Southampton Central", "SOU"),
//        Station("Southampton Central1", "SO1"),
//        Station("Southampton Central2", "SO2")
//    )

    fun getStations() : MutableLiveData<List<Station>> {
        if (stations.isNullOrEmpty()){
            loadStations()
        }
        mutableStations.value = stations
        return mutableStations
    }

    fun getStation(stationStub: StationStub?) : Station? {
        if (stationStub == null) return null
        return stationCodeLookup[stationStub.name]
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

        //val file = context.resources.openRawResource(R.raw.station_codes)
        //stations = Klaxon().parseArray(file) ?: stations
    }

    private class LoadStationsFromJson : AsyncTask<InputStream, Int, List<Station>>() {

        override fun doInBackground(vararg params: InputStream): List<Station>? {
            return Klaxon().parseArray<Station>(params[0])
        }

        override fun onPostExecute(result: List<Station>?) {
            //stations = result ?: stations
            mutableStations.value = stations
        }
    }

    object SearchManager{

        private var recentStations = ArrayList<Station>()

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
            return stationCodeLookup[stationSuggestion.code]
        }

        fun findSuggestions(
            query: String,
            limit: Int,
            listener: stationSuggestionListener?
        ) {
            object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
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