package com.anfantanion.traintimes1.models.stationResponse

import com.anfantanion.traintimes1.models.parcelizable.ServiceStub

data class Association (
    val type : String, // "join" "divide"
    val associatedUid : String,
    val associatedRunDate : String
){
    fun toServiceStub() : ServiceStub{
        return ServiceStub(associatedUid,associatedRunDate)
    }
}
