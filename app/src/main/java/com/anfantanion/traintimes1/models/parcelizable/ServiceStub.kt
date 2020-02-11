package com.anfantanion.traintimes1.models.parcelizable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceStub(
    val serviceUid: String,
    val runDate: String
) : Parcelable