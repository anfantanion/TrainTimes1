package com.anfantanion.traintimes1.models

import java.io.Serializable
import java.util.*

class TimeDate(startDate: String? = null, startTime: String? = null) : Serializable, Comparable<TimeDate>{
    var calendar = Calendar.getInstance()

    companion object {
        private const val serialVersionUID: Long = 1
    }

    init{
        startTime?.let{setTime(it)}
    }

    fun setTime(string: String){
        calendar.set(Calendar.HOUR_OF_DAY, (string.substring(0,2)).toInt())
        calendar.set(Calendar.MINUTE, (string.substring(2,4)).toInt())
    }

    fun getDate():String{
        return dateToString(false)
    }

    fun getDateTime():String{
        return dateToString(true)
    }

    fun getTime():String{
        return ("${padInt(calendar.get(Calendar.HOUR_OF_DAY))}${padInt(calendar.get(Calendar.MINUTE))}")
    }

    fun resetDate(){
        val now = Calendar.getInstance()
        calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
    }

    fun resetTime(){
        val now = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE))
    }

    fun addMinutes(minutes: Int){
        calendar.add(Calendar.MINUTE,minutes)
    }

    private fun dateToString(doTime: Boolean) : String{
        val sb = StringBuilder()
        sb.append("${calendar.get(Calendar.YEAR)}/${padInt(calendar.get(Calendar.MONTH)+1)}/${padInt(calendar.get(Calendar.DAY_OF_MONTH))}")
        if (doTime)
            sb.append("/${padInt(calendar.get(Calendar.HOUR_OF_DAY))}${padInt(calendar.get(Calendar.MINUTE))}")
        return sb.toString()
    }

    private fun padInt(int : Int) : String{
        return int.toString().padStart(2,'0')
    }

    override fun compareTo(other: TimeDate): Int {
        return this.calendar.timeInMillis.compareTo(other.calendar.timeInMillis)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimeDate

        if (calendar.timeInMillis != other.calendar.timeInMillis) return false

        return true
    }


}