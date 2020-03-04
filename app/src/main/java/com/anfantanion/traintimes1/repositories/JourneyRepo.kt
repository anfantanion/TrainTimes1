package com.anfantanion.traintimes1.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.models.parcelizable.JourneyStub
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object JourneyRepo {

    //var activeJourney: ActiveJourney? = null
    var activeJourney = MutableLiveData<ActiveJourney?>(null)

    var timer = Timer("Active Journey Update Timer")
    var timerIsPresent = true
    const val updateTime = 60000L

    var orderedJourneys = ArrayList<Journey>()
    var journeyLookup = HashMap<UUID,Journey>()
    private const val journeyFIleName = "journeys"

    fun startUpdateTimer(){
//        if (!timerIsPresent) {
//            timerIsPresent = true
//            timer.schedule(ActiveJourneyUpdateTimer(), 10000, updateTime)
//        }
    }

    fun setActiveJourney(activeJourney: ActiveJourney?){
        this.activeJourney.value = activeJourney
        Log.d("JourneyREPO","STARted timer")
        startUpdateTimer()
    }

    fun addJourney(journey: Journey){
        if (journey.uuid in journeyLookup){
            orderedJourneys[orderedJourneys.indexOf(journey)] = journey
            journeyLookup[journey.uuid] = journey
        }
        else {
            orderedJourneys.add(journey)
            journeyLookup[journey.uuid] = journey
        }
    }

    fun getFavourites(): List<Journey>{
        return orderedJourneys.filter{it.favourite}
    }

    fun swapOrder(i : Int, j: Int){
        orderedJourneys[j] = orderedJourneys[i].also {orderedJourneys[i] = orderedJourneys[j]}
    }

    fun getJourney(journeyStub: JourneyStub?): Journey?{
        journeyStub?.let { return journeyLookup[journeyStub.uuid] }
        return null
    }

    fun removeJourney(journey: Journey){
        orderedJourneys.remove(journey)
        journeyLookup.remove(journey.uuid)
    }

    fun getSavedJourneys(): List<Journey>{
        return orderedJourneys
    }

    fun save(context: Context){
        context.openFileOutput(journeyFIleName,Context.MODE_PRIVATE).use{
            ObjectOutputStream(it).use{ it2 ->
                it2.writeObject(orderedJourneys)
                it2.writeObject(activeJourney.value)
            }
        }
    }

    fun load(context: Context){
        try {
            context.openFileInput(journeyFIleName).use { fis ->
                ObjectInputStream(fis).use { it2 ->
                    orderedJourneys = it2.readObject() as? ArrayList<Journey> ?: ArrayList()
                    activeJourney.value = it2.readObject() as ActiveJourney?
                }
            }
        } catch (e: Exception){
            Log.d("SEARCHMANAGER","History File not found ${e.localizedMessage}")
        }
        for (j in orderedJourneys) journeyLookup[j.uuid] = j
    }

    class ActiveJourneyUpdateTimer() : TimerTask(){
        override fun run() {
            Log.d("JourneyREPO","Updating")
            val aJ = activeJourney.value
            if (aJ == null) {
                cancel()
                timerIsPresent = false
                return
            }
            if (aJ.journeyPlan.isNotEmpty()) {
                aJ.getServiceResponses(
                    listener = {
                        activeJourney.value = activeJourney.value //Trigger all observers.
                    },
                    errorListener = Response.ErrorListener {

                    }
                )
            }

        }

    }



}