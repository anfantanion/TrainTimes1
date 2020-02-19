package com.anfantanion.traintimes1.ui.serviceDetails

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.stationResponse.LocationDetail
import kotlinx.android.synthetic.main.fragment_service_details_listitem.view.*

class ServiceDetailsRecyclerAdapter (
    private val serviceDetailsRecycClick: ServiceDetailsRecycClick,
    var timeDisplayType: TimeView = TimeView()
) : RecyclerView.Adapter<ServiceDetailsRecyclerAdapter.ViewHolder>(){

    var locations = emptyList<LocationDetail>()


    class ViewHolder(
        itemView: View,
        private val serviceDetailsRecycClick: ServiceDetailsRecycClick
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        val image = itemView.serviceDetailsImageView
        val stationCode = itemView.serviceDetailsTextStationCode
        val stationName = itemView.serviceDetailsTextStationName
        val platformNo = itemView.serviceDetailsTimingInfoPlatform

        val timingPlanned = itemView.serviceDetailsTimingInfoPlanned
        val timingPlannedArrival = itemView.serviceDetailsTimingInfoPlannedArrival
        val timingPlannedDepart = itemView.serviceDetailsTimingInfoPlannedDepart

        val timingReal = itemView.serviceDetailsTimingInfoRealTime
        val timingRealArrival = itemView.serviceDetailsTimingInfoRealTimeArrival
        val timingRealDepart = itemView.serviceDetailsTimingInfoRealTimeDepart
        val timingRealDelay = itemView.serviceDetailsTimingInfoRealTimeDelay

        override fun onClick(v: View?) {
            serviceDetailsRecycClick.onStationClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_service_details_listitem,parent,false)
        return ViewHolder(view,serviceDetailsRecycClick)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]

        when(timeDisplayType.current){
            TimeView.Types.REALTIME -> {
                holder.timingPlanned.visibility = View.GONE
                holder.timingReal.visibility = View.VISIBLE
            }
            TimeView.Types.BOOKEDTIME -> {
                holder.timingPlanned.visibility = View.VISIBLE
                holder.timingReal.visibility = View.GONE
            }
            TimeView.Types.NONE -> {
                holder.timingPlanned.visibility = View.GONE
                holder.timingReal.visibility = View.GONE
            }
        }

        holder.stationCode.text = location.crs
        holder.stationName.text = location.description
        holder.platformNo.text = location.platform

        holder.timingPlannedArrival.text = location.gbttBookedArrival
        holder.timingPlannedDepart.text = location.gbttBookedDeparture

        holder.timingRealArrival.text = location.getArrivalTime()
        holder.timingRealDepart.text = location.getDepartureTime()
        holder.timingRealDelay.text = location.delayString()

        if (location.realtimeArrivalActual == true)
            holder.timingRealArrival.setTypeface(null,Typeface.BOLD)
        else
            holder.timingRealArrival.setTypeface(null,Typeface.NORMAL)

        if (location.realtimeDepartureActual == true)
            holder.timingRealDepart.setTypeface(null,Typeface.BOLD)
        else
            holder.timingRealDepart.setTypeface(null,Typeface.NORMAL)

        if (location.delay() ?: 0 > 10)
            holder.timingRealDelay.setTypeface(null,Typeface.BOLD)
        else
            holder.timingRealDelay.setTypeface(null,Typeface.NORMAL)

        when (position){
            0 -> holder.image.setImageResource(R.drawable.ic_servicedetails_start)
            locations.size-1 -> holder.image.setImageResource(R.drawable.ic_servicedetails_end)
            else -> holder.image.setImageResource(R.drawable.ic_serivcedetails_station)
        }
    }

    interface ServiceDetailsRecycClick{
        fun onStationClick(position: Int)
    }

    class TimeView(
        var current: Types = Types.REALTIME
    ){

        enum class Types { REALTIME, BOOKEDTIME, NONE}

        fun next() : Types{
            current = when (current){
                Types.REALTIME -> Types.BOOKEDTIME
                Types.BOOKEDTIME -> Types.NONE
                Types.NONE -> Types.REALTIME
            }
            return current
        }

    }


}