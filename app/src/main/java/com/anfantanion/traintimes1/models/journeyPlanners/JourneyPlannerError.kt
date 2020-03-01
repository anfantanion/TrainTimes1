package com.anfantanion.traintimes1.models.journeyPlanners

import com.android.volley.VolleyError
import com.anfantanion.traintimes1.models.parcelizable.StationStub

class JourneyPlannerError (
    var type: ErrorType,
    var reason: String,
    var errors : List<StationStub>? = null,
    var volleyError: VolleyError? = null

) {
    enum class ErrorType {
        NOSERVICEFOUND,
        VOLLEYERROR,
        OTHER,
    }
}