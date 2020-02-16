package com.anfantanion.traintimes1.repositories

import android.content.Context
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.anfantanion.traintimes1.repositories.cachedb.CStationResponse
import com.anfantanion.traintimes1.repositories.cachedb.CacheDatabase

object RTTAPI{
    val endpoint = "https://api.rtt.io/api/v1/json"
    private const val locationQuery = "/search"
    private const val serviceQuery = "/service"

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
        val request = buildStationRequest(station,to,from,date)
        val req = VolleyStationRequest(Request.Method.GET,request,MiniStationRepsonse(listener,station,to,from,date),errorListener)
        req.setShouldCache(false)
        StationRepo.volleyRequestQueue.add(req)

    }

    private fun buildStationRequest(station : String,
                            to : String?,
                            from : String?,
                            date : String?) : String{
        val urlBuilder = StringBuilder().append(endpoint,locationQuery,"/",station)
        if (to != null) urlBuilder.append("/to/",to)
        if (from != null) urlBuilder.append("/from/",from)
        if (date != null) urlBuilder.append("/",date)
        return urlBuilder.toString()
    }

    fun requestService(
        serviceStub: ServiceStub,
        listener: Response.Listener<ServiceResponse>,
        errorListener: Response.ErrorListener?
    ){
        val request = buildServiceRequest(serviceStub.serviceUid,serviceStub.runDate)
        val req = VolleyServiceRequest(Request.Method.GET,request,MiniServiceRepsonse(listener,serviceStub.serviceUid,serviceStub.runDate),errorListener)
        req.setShouldCache(false)
        StationRepo.volleyRequestQueue.add(req)
    }

    fun buildServiceRequest(
        serviceUID : String,
        runDate: String
    ) : String{
        return "$endpoint$serviceQuery/$serviceUID/$runDate"
    }

    /**
     * Intercepts response and adds it to database.
     */
    class MiniStationRepsonse(
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

    class MiniServiceRepsonse(
        val rl : Response.Listener<ServiceResponse>,
        val serviceUID : String,
        val runDate: String
    ) : Response.Listener<ServiceResponse>  {
        override fun onResponse(response: ServiceResponse) {
            //cacheDatabase.CStationResponceDao().insert(CStationResponse(station,System.currentTimeMillis(),to,from,date,response))
            rl.onResponse(response)
        }
    }


}