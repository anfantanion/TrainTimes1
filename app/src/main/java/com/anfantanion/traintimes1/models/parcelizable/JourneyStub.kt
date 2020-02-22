package com.anfantanion.traintimes1.models.parcelizable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class JourneyStub(
    val uuid: UUID
) : Parcelable, Serializable