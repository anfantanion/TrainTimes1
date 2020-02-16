package com.anfantanion.traintimes1.ui.serviceDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.stationResponse.LocationDetail
import kotlinx.android.synthetic.main.fragment_service_details_listitem.view.*

class ServiceDetailsRecyclerAdapter () : RecyclerView.Adapter<ServiceDetailsRecyclerAdapter.ViewHolder>(){

    var locations = emptyList<LocationDetail>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_service_details_listitem,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]

        holder.stationCode.text = location.crs
        holder.stationName.text = location.description
        holder.platformNo.text = location.platform

        holder.timingRealArrival.text = location.getArrivalTime()
        holder.timingRealDepart.text = location.getDepartureTime()
        holder.timingRealDelay.text = location.delayString()

        when (position){
            0 -> holder.image.setImageResource(R.drawable.ic_servicedetails_start)
            locations.size-1 -> holder.image.setImageResource(R.drawable.ic_servicedetails_end)
            else -> holder.image.setImageResource(R.drawable.ic_serivcedetails_station)
        }
    }

}