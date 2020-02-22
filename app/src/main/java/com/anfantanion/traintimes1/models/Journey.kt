package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlanner
import com.anfantanion.traintimes1.models.parcelizable.JourneyStub
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.repositories.StationRepo
import java.io.Serializable
import java.util.*

class Journey (
    vararg var waypoints : StationStub,
    var givenName: String? = null,
    var type: Type = Type.DYNAMIC
): Serializable{
    var journeyPlan = emptyList<ServiceStub>()
    var uuid = UUID.randomUUID()

    enum class Type{
        DYNAMIC, STARTAT, ARRIVEBY
    }

    fun getOriginName() : String{
        return StationRepo.getStation(waypoints[0])!!.name
    }

    fun getIntermidateName() : String?{
        if (waypoints.size<3) return null
        val stringBuilder = StringBuilder()
        var seperator = ""
        waypoints.forEachIndexed { i: Int, stationStub: StationStub ->
            if (!(i==0 || i==waypoints.size-1)){
                stringBuilder.append(seperator)
                stringBuilder.append(StationRepo.getStation(stationStub)!!.name)
                seperator = ","
            }
        }
        return stringBuilder.toString()
    }

    fun getDestName() : String{
        return StationRepo.getStation(waypoints[waypoints.size-1])!!.name
    }

    fun plan(journeyListener: JourneyPlanner.JourneyListener){
        val x = JourneyPlanner()
        x.journeyListener = journeyListener
        x.plan(waypoints.toList())
    }

    fun toJourneyStub(): JourneyStub{
        return JourneyStub(uuid)
    }






}