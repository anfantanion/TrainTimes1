package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.stationResponse.Destination
import java.lang.StringBuilder

fun destinationName(destination: List<Destination>):String{
    if (destination.size == 1) return destination[0].description

    val stringBuilder = StringBuilder()

    var separator = ""
    destination.forEach{ dest: Destination ->
        stringBuilder.append(separator)
        stringBuilder.append(dest.description)
        separator = " & "
    }
    return stringBuilder.toString()
}