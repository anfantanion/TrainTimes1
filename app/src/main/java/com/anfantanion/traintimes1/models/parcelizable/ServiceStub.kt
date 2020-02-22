package com.anfantanion.traintimes1.models.parcelizable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ServiceStub(
    val serviceUid: String,
    val runDate: String
) : Parcelable, Serializable