package com.anfantanion.traintimes1.ui.serviceDetails

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.models.stationResponse.LocationDetail
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import kotlinx.android.synthetic.main.fragment_service_details_listitem.view.*
import kotlin.math.max
import kotlin.math.min

class ServiceDetailsRecyclerAdapter (
    val viewHolderListener: ViewHolder.ViewHolderListener,
    var timeDisplayType: TimeView = TimeView()
) : RecyclerView.Adapter<ServiceDetailsRecyclerAdapter.ViewHolder>(){

    var serviceResponse : ServiceResponse? = null
    set(value) {
        locations =  value?.locations ?: emptyList()
        lastKnown = value?.getMostRecentLocation()?.let{value.getPositionOfStation(it.toStationStub())}
        field = value
    }

    var lastKnown : Int? = null
    var locations = emptyList<LocationDetail>()
    var focused = emptyList<StationStub>()


    class ViewHolder(
        itemView: View,
        private val viewHolderListener: ViewHolderListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val image = itemView.serviceDetailsImageView!!
        val stationCode = itemView.serviceDetailsTextStationCode!!
        val stationName = itemView.serviceDetailsTextStationName!!
        val platformNo = itemView.serviceDetailsTimingInfoPlatform!!

        val timingPlanned = itemView.serviceDetailsTimingInfoPlanned!!
        val timingPlannedArrival = itemView.serviceDetailsTimingInfoPlannedArrival!!
        val timingPlannedDepart = itemView.serviceDetailsTimingInfoPlannedDepart!!

        val timingReal = itemView.serviceDetailsTimingInfoRealTime!!
        val timingRealArrival = itemView.serviceDetailsTimingInfoRealTimeArrival!!
        val timingRealDepart = itemView.serviceDetailsTimingInfoRealTimeDepart!!
        val timingRealDelay = itemView.serviceDetailsTimingInfoRealTimeDelay!!

        val serviceDetailsCancelled = itemView.serviceDetailsCancelled!!
        val serviceDetailsCancelledText = itemView.serviceDetailsCancelledText!!
        val serviceDetailsCancelledReason = itemView.serviceDetailsCancelledReason!!

        val serviceDetailsExtension = itemView.serviceDetailsExtension!!
        val serviceDetailsExtensionImage = itemView.serviceDetailsExtensionImage!!
        val serviceDetailsTextAdditionalInfo = itemView.serviceDetailsTextAdditionalInfo!!
        val serviceDetailsTextAdditionalInfoButton = itemView.serviceDetailsTextAdditionalInfoButton!!

        init {
            serviceDetailsTextAdditionalInfoButton.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v){
                serviceDetailsTextAdditionalInfoButton -> viewHolderListener.onAdditionalInfoButtonClick(adapterPosition)
                itemView -> viewHolderListener.onMainClick(adapterPosition)
            }
        }

        interface ViewHolderListener{
            fun onMainClick(position: Int)
            fun onAdditionalInfoButtonClick(position: Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_service_details_listitem,parent,false)
        return ViewHolder(view,viewHolderListener)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val location = locations[position]

        holder.platformNo.visibility = View.VISIBLE
        holder.serviceDetailsCancelled .visibility = View.GONE
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

        //Underline Focused Stations
        if (location.toStationStub() in focused)
            holder.stationName.paintFlags = holder.stationName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        else
            holder.stationName.paintFlags = holder.stationName.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

        holder.platformNo.text = location.platform

        //Color + Bold Changed platforms
        if (location.platformChanged == true) {
            holder.platformNo.setTextColor(ContextCompat.getColor(context, R.color.late))
            holder.timingRealArrival.setTypeface(null, Typeface.BOLD)
        } else {
            holder.platformNo.setTextColor(ContextCompat.getColor(context, R.color.colorTitleText))
            holder.timingRealArrival.setTypeface(null, Typeface.NORMAL)
        }

        holder.timingPlannedArrival.text = location.gbttBookedArrival
        holder.timingPlannedDepart.text = location.gbttBookedDeparture


        holder.timingRealArrival.text = location.getArrivalTime()
        holder.timingRealDepart.text = location.getDepartureTime()
        holder.timingRealDelay.text = location.delayString()


        if (location.realtimeArrivalActual == true)
            holder.timingRealArrival.setTypeface(null,Typeface.BOLD)
        else
            holder.timingRealArrival.setTypeface(null,Typeface.NORMAL)

        if (location.realtimeDepartureActual == true) {
            holder.timingRealDepart.setTypeface(null, Typeface.BOLD)
        }else{
            holder.timingRealDepart.setTypeface(null,Typeface.NORMAL)
        }

        if (location.delay() ?: 0 > 5) {
            holder.timingRealDelay.setTypeface(null, Typeface.BOLD)
            holder.timingRealDelay.setTextColor(ContextCompat.getColor(context,R.color.late))
        }else {
            holder.timingRealDelay.setTypeface(null, Typeface.NORMAL)
            holder.timingRealDelay.setTextColor(ContextCompat.getColor(context,R.color.colorSubtitleText))
        }

        when(location.displayAs){
            "CANCELLED_CALL" -> {
                holder.timingPlanned.visibility = View.GONE
                holder.timingReal.visibility = View.GONE
                holder.timingReal.visibility = View.GONE
                holder.serviceDetailsCancelled .visibility = View.VISIBLE
                holder.platformNo.visibility = View.GONE
                holder.serviceDetailsCancelledText.text = context.getString(R.string.Cancelled,location.cancelReasonCode?:"?")
                holder.serviceDetailsCancelledReason.text = location.cancelReasonShortText?.capitalize() ?: context.getString(R.string.Unknown)
            }

            "STARTS" , "ORIGIN" -> {
                holder.timingRealArrival.text = ""
                holder.timingPlannedArrival.text = ""
            }
        }


        when (position){
            0 -> holder.image.setImageResource(R.drawable.ic_servicedetails_start)
            locations.size-1 -> holder.image.setImageResource(R.drawable.ic_servicedetails_end)
            else -> holder.image.setImageResource(R.drawable.ic_serivcedetails_station)
        }

        //Splitting/ Joining Trains
        if (!location.associations.isNullOrEmpty()){
            holder.serviceDetailsExtension.visibility = View.VISIBLE

            //If Last position hide image
            if (position == locations.size-1)
                holder.serviceDetailsExtensionImage.visibility = View.INVISIBLE
            else
                holder.serviceDetailsExtensionImage.visibility = View.VISIBLE

            holder.serviceDetailsTextAdditionalInfo.visibility = View.GONE

            when (location.associations[0].type){
                "join" -> {
                    val otherDest = if (serviceResponse?.origin?.get(0) == location.origin.get(0))
                        serviceResponse?.origin?.get(1)?.description
                    else
                        serviceResponse?.origin?.get(0)?.description

                    holder.serviceDetailsTextAdditionalInfoButton.text = context.getString(R.string.serviceDetailsTrainServiceFrom, otherDest)

                }
                "divide" -> {
                    val otherDest = if (serviceResponse?.destination?.get(0) == location.destination.get(0))
                        serviceResponse?.destination?.get(1)?.description
                    else
                        serviceResponse?.destination?.get(0)?.description

                    holder.serviceDetailsTextAdditionalInfo.text =
                        context.getString(R.string.serviceDetailsTrainDivide)
                    holder.serviceDetailsTextAdditionalInfoButton.text = context.getString(R.string.serviceDetailsTrainServiceTo, otherDest ?: "")
                }
            }


        }else{
            holder.serviceDetailsExtension.visibility = View.GONE
        }

        //

        if (position <= lastKnown ?: 0 ){
            holder.image.setColorFilter(ContextCompat.getColor(context, R.color.stationPassed))
        }else{
            holder.image.setColorFilter(ContextCompat.getColor(context, R.color.stationNormal))
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