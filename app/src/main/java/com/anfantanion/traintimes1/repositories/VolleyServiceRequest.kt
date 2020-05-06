package com.anfantanion.traintimes1.repositories

import android.content.res.Resources
import android.util.Base64
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.keys
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.nio.charset.Charset
import java.util.*

class VolleyServiceRequest(method: Int,
                           url : String,
                           var listener : Listener<ServiceResponse>,
                           errorListener: ErrorListener?
) : Request<ServiceResponse>(method,url,errorListener) {


    override fun parseNetworkResponse(response: NetworkResponse?): Response<ServiceResponse> {
        val parsed = String(response!!.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
        try {
            val gsoned = Gson().fromJson(parsed, ServiceResponse::class.java)
            return Response.success(gsoned,null)
        }catch (e : JsonSyntaxException){
            return Response.error(VolleyError(e))
        }


    }

    override fun deliverResponse(response: ServiceResponse?) {
        listener.onResponse(response)
    }

    override fun getHeaders(): Map<String, String>? {
        val headers = HashMap<String, String>()
        val auth = (
                "Basic " + Base64.encodeToString(
                    RTTAPI.context.getString(R.string.rttapi_cred).toByteArray(),
                    Base64.NO_WRAP
                ))
        headers["Authorization"] = auth
        return headers
    }
}