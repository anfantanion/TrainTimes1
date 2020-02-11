package com.anfantanion.traintimes1.repositories;

import android.util.Base64;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CustomStringRequest extends StringRequest {

    private String username;
    private String password;

    public CustomStringRequest(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener, String username, String password ) {
        super(method, url, listener, errorListener);
        this.password=password;
        this.username=username;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        //add params <key,value>
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        String credentials = username+":"+password;
        String auth = "Basic "
                + Base64.encodeToString(credentials.getBytes(),
                Base64.NO_WRAP);
        headers.put("Authorization", auth);
        return headers;
    }

}
