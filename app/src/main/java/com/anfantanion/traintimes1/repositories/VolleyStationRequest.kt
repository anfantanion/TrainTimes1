package com.anfantanion.traintimes1.repositories

import android.util.Base64
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.toolbox.HttpHeaderParser
import com.anfantanion.traintimes1.keys
import com.anfantanion.traintimes1.models.stationResponse.StationResponse
import com.google.gson.Gson
import java.nio.charset.Charset
import java.util.*

class VolleyStationRequest(method: Int,
                           url : String,
                           var listener : Listener<StationResponse>,
                           errorListener: ErrorListener?
) : Request<StationResponse>(method,url,errorListener) {


    override fun parseNetworkResponse(response: NetworkResponse?): Response<StationResponse> {
        val parsed = String(response!!.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
        return Response.success(Gson().fromJson(parsed,StationResponse::class.java),null)
    }

    override fun deliverResponse(response: StationResponse?) {
        listener.onResponse(response)
    }

    override fun getHeaders(): Map<String, String>? {
        val headers = HashMap<String,String>()
        val credentials: String = keys.user + ":" + keys.pass
        val auth = (
                "Basic "
                + Base64.encodeToString(
                credentials.toByteArray(),
                Base64.NO_WRAP
        ))
        headers["Authorization"] = auth
        return headers
    }
}