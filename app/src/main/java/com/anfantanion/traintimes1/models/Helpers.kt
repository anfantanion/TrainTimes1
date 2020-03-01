package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.stationResponse.Destination
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

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

fun differenceOfTimesMinutes(start: String, end: String): Int{
    val actual = TimeDate(startTime = start)
    val booked = TimeDate(startTime = end)
    return TimeUnit.MILLISECONDS.toMinutes(
        actual.calendar.timeInMillis - booked.calendar.timeInMillis).toInt()
}

fun differenceOfTimesMinutes(start:Long, end: Long): Int{
    return TimeUnit.MILLISECONDS.toMinutes(
        start-end
    ).toInt()
}