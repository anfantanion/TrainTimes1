package com.anfantanion.traintimes1.ui.activeJourney

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.differenceOfTimesMinutes
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.ui.serviceDetails.ServiceDetailsRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_active_journey_listitem.view.*

class ActiveJourneyRecyclerAdapter(
    var viewHolderListener: ActiveJourneyViewHolder.ViewHolderListener
) : RecyclerView.Adapter<ActiveJourneyRecyclerAdapter.ActiveJourneyViewHolder>() {

    var services = emptyList<ServiceResponse>()
    var waypoints = emptyList<Station>()


    class ActiveJourneyViewHolder(itemView: View, private var viewHolderListener: ViewHolderListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnTouchListener{
        val titleLayout = itemView.activeJourneyListItemTitleLayout!!
        val title = itemView.activeJourneyListItemTitle!!

        val change = itemView.activeJourneyListItemChangeAt!!

        val activeJourneyListItemRecycler = itemView.activeJourneyListItemRecycler!!

        val serviceDetailsButton = itemView.activeJourneyListItemButtonServiceDetails !!
        val mapButton = itemView.activeJourneyListItemButtonMap!!
        val refreshButton = itemView.activeJourneyListItemRefresh!!
        val expandButton = itemView.activeJourneyListItemExpand!!

        var serviceDetailsRecyclerAdapter = ServiceDetailsRecyclerAdapter(
            NullListener(),
            ServiceDetailsRecyclerAdapter.TimeView(ServiceDetailsRecyclerAdapter.TimeView.Types.REALTIME))

        var expanded = false
        val initialHeight = activeJourneyListItemRecycler.layoutParams.height





        init {
            activeJourneyListItemRecycler.layoutManager = LinearLayoutManager(activeJourneyListItemRecycler.context)
            activeJourneyListItemRecycler.adapter = serviceDetailsRecyclerAdapter
            itemView.setOnClickListener(this)
            itemView.setOnTouchListener(this)
            serviceDetailsButton.setOnClickListener(this)
            mapButton.setOnClickListener(this)
            expandButton.setOnClickListener(this)
            refreshButton.setOnClickListener(this)
        }

        //On Expand Button Pressed



        override fun onClick(v: View?) {
            when (v){
                serviceDetailsButton -> viewHolderListener.onDetailsButtonClick(adapterPosition)
                activeJourneyListItemRecycler -> viewHolderListener.onDetailsButtonClick(adapterPosition)
                mapButton -> viewHolderListener.onMapButtonClick(adapterPosition)
                expandButton -> {onExpandClick() ; viewHolderListener.onExpandClick(adapterPosition) }
                refreshButton -> viewHolderListener.onRefreshClick(adapterPosition)
                else -> viewHolderListener.onItemClick(adapterPosition)
            }

        }

        fun onExpandClick(){
            expanded = !expanded
            if (!expanded){
                activeJourneyListItemRecycler.layoutParams.height = initialHeight
                activeJourneyListItemRecycler.requestLayout()
                expandButton.setImageResource(R.drawable.ic_expand_more_black_24dp)
            }else {
                activeJourneyListItemRecycler.layoutParams.height = initialHeight*6
                activeJourneyListItemRecycler.requestLayout()
                expandButton.setImageResource(R.drawable.ic_expand_less_black_24dp)
            }
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                viewHolderListener.dragImageTouchDown(this,adapterPosition)
            }
            return false
        }

        interface ViewHolderListener{
            fun onItemClick(position: Int)
            fun onDetailsButtonClick(position: Int)
            fun onMapButtonClick(position: Int)
            fun onExpandClick(position: Int)
            fun onRefreshClick(position: Int)
            fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveJourneyRecyclerAdapter.ActiveJourneyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_active_journey_listitem,parent,false)
        return ActiveJourneyRecyclerAdapter.ActiveJourneyViewHolder(view, viewHolderListener)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ActiveJourneyRecyclerAdapter.ActiveJourneyViewHolder, position: Int) {
        val context = holder.itemView.context
        val service = services[position]

        holder.title.text = service.getName(waypoints[position].toStationStub())
        if (position+1 < waypoints.size-1){
            var oldSArrival =  services[position].getRTStationArrival(waypoints[position+1].toStationStub())
            var time = differenceOfTimesMinutes(TimeDate(startTime = oldSArrival),TimeDate())

            if (time < 0){
                holder.change.visibility = View.GONE
            }else {
                holder.change.text = context.resources.getQuantityString(
                    R.plurals.activeJourneyChangeAt2,
                    time,
                    waypoints[position + 1].name,
                    time
                )
                holder.change.visibility = View.VISIBLE
            }
        }
        else
            holder.change.visibility = View.GONE





        holder.serviceDetailsRecyclerAdapter.locations = service.locations?: emptyList()
        val last = service.getMostRecentLocation()?.toStationStub()
        holder.serviceDetailsRecyclerAdapter.focused = listOfNotNull(last,waypoints[position + 1].toStationStub())
        if (last!=null) {
            val x = service.getPositionOfStation(last)
            x?.let { holder.activeJourneyListItemRecycler.layoutManager!!.scrollToPosition(it) }
        }
        holder.activeJourneyListItemRecycler.visibility = View.VISIBLE
        holder.serviceDetailsRecyclerAdapter.notifyDataSetChanged()


    }

}

class NullListener() : ServiceDetailsRecyclerAdapter.ViewHolder.ViewHolderListener {
    override fun onMainClick(position: Int) {

    }

    override fun onAdditionalInfoButtonClick(position: Int) {

    }

}