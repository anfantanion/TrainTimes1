package com.anfantanion.traintimes1.models.parcelizable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ServiceStub(
    val serviceUid: String,
    val runDate: String
) : Parcelable, Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceStub

        if (serviceUid != other.serviceUid) return false
        if (runDate != other.runDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serviceUid.hashCode()
        result = 31 * result + runDate.hashCode()
        return result
    }
}