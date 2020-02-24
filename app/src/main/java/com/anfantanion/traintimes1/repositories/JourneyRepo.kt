package com.anfantanion.traintimes1.repositories

import android.content.Context
import android.util.Log
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.models.parcelizable.JourneyStub
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object JourneyRepo {

    var orderedJourneys = ArrayList<Journey>()
    var journeyLookup = HashMap<UUID,Journey>()
    private const val journeyFIleName = "journeys"


    fun addJourney(journey: Journey){
        if (journey.uuid in journeyLookup){
            orderedJourneys.add(orderedJourneys.indexOf(journey),journey)
            journeyLookup[journey.uuid] = journey
        }
        else {
            orderedJourneys.add(journey)
            journeyLookup[journey.uuid] = journey
        }
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
            }
        }
    }

    fun load(context: Context){
        try {
            context.openFileInput(journeyFIleName).use { fis ->
                ObjectInputStream(fis).use { it2 ->
                    orderedJourneys = it2.readObject() as? ArrayList<Journey> ?: ArrayList<Journey>()
                }
            }
        } catch (e: Exception){
            Log.d("SEARCHMANAGER","History File not found ${e.localizedMessage}")
            orderedJourneys = ArrayList<Journey>()
        }
        for (j in orderedJourneys) journeyLookup[j.uuid] = j
    }



}