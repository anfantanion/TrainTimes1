package com.anfantanion.traintimes1.models

import com.anfantanion.traintimes1.models.journeyPlanners.JourneyPlanner
import com.anfantanion.traintimes1.models.parcelizable.JourneyStub
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.RTTAPI
import com.anfantanion.traintimes1.repositories.StationRepo
import java.io.Serializable
import java.util.*

class Journey (
    vararg var waypoints : StationStub,
    var givenName: String? = null,
    var type: Type = Type.DYNAMIC,
    var time: String? = null
): Serializable{

    var journeyPlan = emptyList<ServiceStub>()
    @Transient var journeyPlanServices = emptyList<ServiceResponse>()
    var uuid = UUID.randomUUID()

    enum class Type{
        DYNAMIC, DEPARTAT, ARRIVEBY
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

    fun plan(journeyListener: (List<ServiceStub>?) -> (Unit)){
        val x = when (type){
            Type.DYNAMIC -> JourneyPlanner(journeyListener)
            Type.ARRIVEBY -> JourneyPlanner(journeyListener)
            Type.DEPARTAT -> JourneyPlanner(journeyListener)
        }
        x.plan(waypoints.toList())
    }

    fun getPlannedServices(journeyListener: (List<ServiceStub>?) -> (Unit)) {
        if (journeyPlan.isEmpty()) plan(journeyListener)
        else journeyListener(journeyPlan)
    }

    fun toJourneyStub(): JourneyStub{
        return JourneyStub(uuid)
    }

    fun getCopy() : Journey{
        return Journey(
            *waypoints,
            givenName = this.givenName,
            type = this.type,
            time = this.time
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Journey

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid?.hashCode() ?: 0
    }


}