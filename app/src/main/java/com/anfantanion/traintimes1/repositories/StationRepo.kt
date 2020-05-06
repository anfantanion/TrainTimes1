package com.anfantanion.traintimes1.repositories

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object StationRepo {


    private lateinit var activity: Activity
    private var mutableStations = MutableLiveData<List<Station>>()
    private var stations = ArrayList<Station>()
    private var stationCodeLookup = HashMap<String, Station>()

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var lastLocation : Location? = null

    private lateinit var context: Context
    private var rttAPI = RTTAPI
    lateinit var volleyRequestQueue: RequestQueue


    fun setContext(context: Context) {
        this.context = context
        volleyRequestQueue = Volley.newRequestQueue(context)
        rttAPI.context = context
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        SearchManager.load()
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
                if (line.startsWith("//")) continue
                val linesplit = line.split(":")
                val station = Station(linesplit[0],linesplit[1],linesplit[2].toDouble(),linesplit[3].toDouble())
                stations.add(station)
                stationCodeLookup[station.code] = station
            }
        }
        mutableStations.value = stations
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
    }


    object SearchManager{

        private const val historyFileName = "history"
        private var locationStationSuggestionListener : StationSuggestionListener? = null
        private var locationLimit : Int? = null
        private var recentStations = ArrayList<Station>()
        var lastSearchTopStationSuggestion : Station.StationSuggestion? = null
        private lateinit var history : History

        fun getHistory(count: Int): List<Station.StationSuggestion>{
            return history.getHistory(count)
        }

        fun addHistory(stationSuggestion: Station.StationSuggestion){
            return history.add(stationSuggestion)
        }

        fun getStation(stationSuggestion: Station.StationSuggestion) : Station? {
            return stationCodeLookup[stationSuggestion.code]
        }

        fun save(){
            context.openFileOutput(historyFileName,Context.MODE_PRIVATE).use{
                ObjectOutputStream(it).use{it2 ->
                    it2.writeObject(history)
                }
            }
        }

        fun load(){
            try {
                context.openFileInput(historyFileName).use { fis ->
                    ObjectInputStream(fis).use { it2 ->
                        history = it2.readObject() as? History ?: History()
                    }
                }
            } catch (e: Exception){
                Log.d("SEARCHMANAGER","History File not found ${e.localizedMessage}")
                history = History()
            }
        }

        fun findSuggestions(
            query: String,
            limit: Int,
            listener: StationSuggestionListener?
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

        fun findNearbyLast(){
            val x = locationStationSuggestionListener
            if (x!= null)
                findNearby(x, locationLimit)
        }

        fun findNearby(
            listener: StationSuggestionListener,
            limit: Int?
        ) {
            if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                locationStationSuggestionListener = listener
                locationLimit = limit
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    0
                )
                listener.onError(LocationError.NOPERMISSION)
            }else{
                fusedLocationClient.lastLocation.addOnSuccessListener{
                    lastLocation = it
                    if (it!=null){
                        var result = stations.toMutableList()
                        result.sortBy {station ->
                            station.distanceToKM(Math.toRadians(it.latitude),Math.toRadians(it.longitude))
                        }
                        if (limit != null && result.size > limit)
                            result = result.take(limit).toMutableList()
                        val result2 = result.map { station -> station.getStationSuggestion()  }
                        listener.onResults(result2)
                    }else{
                        listener.onError(LocationError.OTHER)
                    }
                }
            }




        }

        interface StationSuggestionListener {
            fun onResults(results: List<Station.StationSuggestion>)
            fun onError(reason: LocationError)
        }

        enum class LocationError{
            NOPERMISSION,
            OTHER
        }

        private class History(val size: Int = 3): Serializable{

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
                if (stationSuggestion !in history) {
                    history[pointer] = stationSuggestion
                    pointer = (pointer + 1) % 3
                }
            }

        }

    }

}