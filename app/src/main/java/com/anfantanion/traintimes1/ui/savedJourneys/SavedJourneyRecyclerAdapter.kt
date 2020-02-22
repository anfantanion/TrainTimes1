package com.anfantanion.traintimes1.ui.savedJourneys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Journey
import kotlinx.android.synthetic.main.fragment_saved_journeys_listitem.view.*

class SavedJourneyRecyclerAdapter(
    val savedJourneyOnClickListener: SavedJourneyRecycClick? = null
) : RecyclerView.Adapter<SavedJourneyRecyclerAdapter.SavedJourneyViewHolder>() {

    var journeys = emptyList<Journey>()

    class SavedJourneyViewHolder(itemView: View, val savedJourneyOnClickListener: SavedJourneyRecycClick?) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }
        val title = itemView.savedJourneysListTitle!!
        val origin = itemView.savedJourneysListOrigin!!
        val to = itemView.savedJourneysListTo!!
        val dest = itemView.savedJourneysListDest!!
        val viaLayout = itemView.savedJourneysListViaLayout!!
        val viaLabel = itemView.savedJourneysListVia!!
        val viaStops = itemView.savedJourneysListViaStops!!
        val timeInfo = itemView.savedJourneysListTimeInfo!!


        override fun onClick(v: View?) {
            savedJourneyOnClickListener?.onSavedJourneyClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedJourneyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_saved_journeys_listitem,parent,false)
        return SavedJourneyViewHolder(view,savedJourneyOnClickListener)
    }

    override fun getItemCount(): Int {
        return journeys.size
    }

    override fun onBindViewHolder(holder: SavedJourneyViewHolder, position: Int) {
        val journey = journeys[position]
        holder.title.text = journey.givenName ?: "Journey "+(position+1)
        holder.origin.text = journey.getOriginName()
        holder.dest.text = journey.getDestName()
        val via = journey.getIntermidateName()
        if (via == null){
            holder.viaLayout.visibility=View.GONE
        }else {
            holder.viaLayout.visibility=View.VISIBLE
            holder.viaStops.text = via
        }

    }

    interface SavedJourneyRecycClick{
        fun onSavedJourneyClick(position: Int)
    }
}