package com.anfantanion.traintimes1.repositories

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.beust.klaxon.Klaxon
import java.io.InputStream

object StationRepo {


    private var mutableStations = MutableLiveData<List<Station>>()
    private var stations = ArrayList<Station>()

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

    fun loadStations(){
        context.resources.openRawResource(R.raw.station_codes_geo_final_rad_android).bufferedReader().useLines {lines ->
            for (line in lines) {
                val linesplit = line.split(":")
                stations.add(Station(linesplit[0],linesplit[1],linesplit[2].toDouble(),linesplit[3].toDouble()))
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

}