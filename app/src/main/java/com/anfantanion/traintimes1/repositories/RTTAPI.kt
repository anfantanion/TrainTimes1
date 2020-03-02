package com.anfantanion.traintimes1.repositories

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.Association
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
        station: StationStub,
        listener: Response.Listener<StationResponse>,
        errorListener: Response.ErrorListener?,
        filter: Filter? = null
    ){
        requestStation(station.crs,listener,errorListener,filter?.to,filter?.from,filter?.date)
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
        requestService(serviceStub,listener,errorListener,true,null)
    }

    private fun requestService(
        serviceStub: ServiceStub,
        listener: Response.Listener<ServiceResponse>,
        errorListener: Response.ErrorListener?,
        interceptResponse: Boolean,
        requireStation : StationStub?
    ){
        val request = buildServiceRequest(serviceStub.serviceUid,serviceStub.runDate)
        val req = if (interceptResponse)
            VolleyServiceRequest(Request.Method.GET,request,MiniServiceRepsonse(listener,serviceStub.serviceUid,serviceStub.runDate,requireStation),errorListener)
        else
            VolleyServiceRequest(Request.Method.GET,request,listener,errorListener)
        req.setShouldCache(false)
        StationRepo.volleyRequestQueue.add(req)
    }

    fun requestServices(
        serviceStubs : List<ServiceStub>,
        destination: StationStub? = null,
        listener: (List<ServiceResponse>) -> (Unit),
        errorListener: Response.ErrorListener?,
        requireStation: StationStub? = null
    ){
        MultiRequest(serviceStubs,listener,errorListener, requireStation).perform()
    }

    fun buildServiceRequest(
        serviceUID : String,
        runDate: String
    ) : String{
        return "$endpoint$serviceQuery/$serviceUID/${runDate.replace('-','/')}"
    }

    data class Filter(val to: String?, val from: String?, val date: String?){

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
        val runDate: String,
        val requireStation : StationStub? = null
    ) : Response.Listener<ServiceResponse>  {
        override fun onResponse(response: ServiceResponse) {
            //Extra processing if service joins another and does not expose information for later stops

            val firstAssociation = response.locations?.first()?.associations
            val lastAssociation = response.locations?.last()?.associations
            when {
                requireStation != null -> {
                    findCounterpartService(response)
                }
                lastAssociation != null -> {
                    findJoiningService(response,lastAssociation[0])
                }
                firstAssociation != null -> {
                    findStartingService(response,firstAssociation[0])
                }
                else -> {
                    rl.onResponse(response)
                }
            }
            //cacheDatabase.CStationResponceDao().insert(CStationResponse(station,System.currentTimeMillis(),to,from,date,response))

        }

        fun findCounterpartService(response: ServiceResponse){

            //Check each location for an association
            var association : Association? = null

            for (it in response.locations!!){
                if (it.crs == requireStation!!.crs) {
                    association = null // Set to null, as it is the correct service.
                    break
                }else {
                    association = it.associations?.get(0)
                    if (association!=null) break
                }
            }


            if (association != null) {
                val x = association!!.toServiceStub()
                requestService(
                    x,
                    listener = Response.Listener {
                        Log.d("RTTAPI",it.toString())
                        rl.onResponse(it)
                    },
                    errorListener = Response.ErrorListener {
                        //If there is an error, just return the original response
                        rl.onResponse(response)
                    },
                    interceptResponse = true,
                    requireStation = null
                )
            } else {
                return rl.onResponse(response)
            }


            response.locations?.forEach{
                if (it.associations?.isNotEmpty() == true){

                }
            }

        }

        fun findJoiningService(response: ServiceResponse, association: Association){
            val lastKnown = response.locations?.last() ?: return
            val originalLocations = response.locations ?: return
            requestService(association.toServiceStub(),
                listener = Response.Listener{ otherResponse ->
                    val otherResponseLocations = otherResponse?.locations
                    if (otherResponseLocations == null) {rl.onResponse(response); return@Listener}
                    val newStart = otherResponseLocations.indexOfFirst{ ld -> ld.crs == lastKnown.crs }
                    val newLocations = otherResponseLocations.subList(newStart+1,otherResponseLocations.size)
                    newLocations.forEach{ld ->
                        ld.origin = lastKnown.origin
                    }
                    val original = originalLocations.toMutableList()
                    original.addAll(newLocations)
                    response.locations = original
                    response.origin = otherResponse.origin
                    rl.onResponse(response)
                },
                errorListener = Response.ErrorListener {
                    //If there is an error, just return the original response
                    rl.onResponse(response)
                },
                interceptResponse = false,
                requireStation = null

            )
        }

        fun findStartingService(response: ServiceResponse, association: Association){
            val firstKnown = response.locations?.first() ?: return
            val originalLocations = response.locations ?: return
            requestService(association.toServiceStub(),
                listener = Response.Listener{ otherResponse ->
                    val otherResponseLocations = otherResponse?.locations
                    if (otherResponseLocations == null) {rl.onResponse(response); return@Listener}
                    val newEnd = otherResponseLocations.indexOfFirst{ ld -> ld.crs == firstKnown.crs }
                    val newLocations = otherResponseLocations.subList(0,newEnd).toMutableList()
                    newLocations.forEach{ld ->
                        ld.destination = firstKnown.destination
                    }
                    response.destination = otherResponse.destination
                    newLocations.addAll(originalLocations)
                    response.locations = newLocations
                    rl.onResponse(response)
                },
                errorListener = Response.ErrorListener {
                    rl.onResponse(response)
                },
                interceptResponse = false,
                requireStation = null
            )
        }
    }

    class MultiRequest(
        val serviceStubs: List<ServiceStub>,
        val listener: (List<ServiceResponse>) -> (Unit),
        val errorListener: Response.ErrorListener?,
        val requireStation: StationStub?,
        val timeOut : Long = 1000000
    ) : Response.Listener<ServiceResponse>, Response.ErrorListener {

        val serviceResponses: MutableList<ServiceResponse?> =
            MutableList(serviceStubs.size) { null }
        val serviceResponses2 = ArrayList<ServiceResponse>()
        var responseCount = 0
        var errorOccured = false

        fun perform() {
            serviceStubs.forEach {
                requestService(it,this,this,true,requireStation)
            }
            Handler().postDelayed({
                if (responseCount != serviceStubs.size && !errorOccured){
                    errorOccured = true
                    errorListener?.onErrorResponse((VolleyError("Timeout after $timeOut ms")))
                }
            },timeOut)
        }

        override fun onResponse(response: ServiceResponse?) {
            response?.let{serviceResponses2.add(it)}
            responseCount++
            //When all responses arrived
            if (responseCount == serviceStubs.size){
                listener(serviceResponses2)
            }
        }

        override fun onErrorResponse(error: VolleyError?) {
            errorOccured = true
            errorListener?.onErrorResponse(error)
        }
    }


}