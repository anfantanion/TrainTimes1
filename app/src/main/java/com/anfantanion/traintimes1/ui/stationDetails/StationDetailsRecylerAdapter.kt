package com.anfantanion.traintimes1.ui.stationDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.stationResponse.Service
import kotlinx.android.synthetic.main.fragment_station_details_listitem.view.*

class StationDetailsRecylerAdapter : RecyclerView.Adapter<StationDetailsRecylerAdapter.ViewHolder>(){

    var services = emptyList<Service>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val time = itemView.stationDetailsLItemTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_station_details_listitem,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = services[position]
        val iv = holder.itemView
        iv.stationDetailsLItemTime.text = service.time()
        iv.stationDetailsLItemName.text = service.destination()
        iv.stationDetailsLItemStatus.text = service.status()
        iv.stationDetailsLItemPlatform.text = service.platform()
    }


}