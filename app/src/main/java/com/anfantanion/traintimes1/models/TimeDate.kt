package com.anfantanion.traintimes1.models

import java.util.*

class TimeDate(startDate: String? = null, startTime: String? = null){
    var calendar = Calendar.getInstance()

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




}