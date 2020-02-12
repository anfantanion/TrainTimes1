package com.anfantanion.traintimes1.repositories

import android.content.Context
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.anfantanion.traintimes1.repositories.cachedb.CStationResponse
import com.anfantanion.traintimes1.repositories.cachedb.CacheDatabase

object RTTAPI{
    val endpoint = "https://api.rtt.io/api/v1/json"
    val locationQuery = "/search"
    val serviceQuery = "/service"

    private lateinit var context: Context
    lateinit var cacheDatabase : CacheDatabase

    fun setContext(context: Context){
        this.context=context
        val temp = Room.databaseBuilder(context,CacheDatabase::class.java, "Station-Response-Cache")
        temp.enableMultiInstanceInvalidation()
        temp.allowMainThreadQueries(        ) //TODO: REMOVE!!!
        temp.fallbackToDestructiveMigration()
        cacheDatabase = temp.build()
    }

    fun requestStation(
        station: String,
        listener: Response.Listener<StationResponse>,
        errorListener: Response.ErrorListener?,
        to: String? = null,
        from: String? = null,
        date: String? = null,
        maxAge: Long = 0
    ) {
        //Check Cache
        //val x = cacheDatabase.CStationResponceDao().getMatching(station,to,from,date,maxAge)
        val x = emptyList<CStationResponse>()
        if(x.isNotEmpty()){
            listener.onResponse(x[0].cObject)
        }

        //Request from api
        val request = buildRequest(station,to,from,date)
        val req = VolleyStationRequest(Request.Method.GET,request,MiniRepsonse(listener,station,to,from,date),errorListener)
        req.setShouldCache(false)
        StationRepo.volleyRequestQueue.add(req)

    }

    fun buildRequest(station : String,
                     to : String?,
                     from : String?,
                     date : String?) : String{
        val urlBuilder = StringBuilder().append(endpoint,locationQuery,"/",station)
        if (to != null) urlBuilder.append("/",to)
        if (from != null) urlBuilder.append("/",from)
        if (date != null) urlBuilder.append("/",date)
        return urlBuilder.toString()
    }

    /**
     * Intercepts response and adds it to database.
     */
    class MiniRepsonse(
        val rl : Response.Listener<StationResponse>,
        val station : String,
        val to : String?,
        val from : String?,
        val date : String?
    ) : Response.Listener<StationResponse>  {
        override fun onResponse(response: StationResponse) {
            //cacheDatabase.CStationResponceDao().insert(CStationResponse(station,System.currentTimeMillis(),to,from,date,response))
            rl.onResponse(response)
        }

    }
}