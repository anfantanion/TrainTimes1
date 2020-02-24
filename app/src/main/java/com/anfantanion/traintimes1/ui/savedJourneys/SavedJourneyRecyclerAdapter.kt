package com.anfantanion.traintimes1.ui.savedJourneys

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Journey
import kotlinx.android.synthetic.main.fragment_saved_journeys_listitem.view.*

class SavedJourneyRecyclerAdapter(
    var viewHolderListener: SavedJourneyViewHolder.ViewHolderListener
) : RecyclerView.Adapter<SavedJourneyRecyclerAdapter.SavedJourneyViewHolder>() {

    var journeys = emptyList<Journey>()

    class SavedJourneyViewHolder(itemView: View, private var viewHolderListener: ViewHolderListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnTouchListener{
        val title = itemView.savedJourneysListTitle!!
        val origin = itemView.savedJourneysListOrigin!!
        val to = itemView.savedJourneysListTo!!
        val dest = itemView.savedJourneysListDest!!
        val viaLayout = itemView.savedJourneysListViaLayout!!
        val viaLabel = itemView.savedJourneysListVia!!
        val viaStops = itemView.savedJourneysListViaStops!!
        val timeInfo = itemView.savedJourneysListTimeInfo!!

        init {
            //itemView.setOnClickListener(this)
            itemView.setOnTouchListener(this)
        }


        override fun onClick(v: View?) {
            viewHolderListener.onSavedJourneyClick(adapterPosition)
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                viewHolderListener.dragImageTouchDown(this,adapterPosition)
            }
            return false
        }

        interface ViewHolderListener{
            fun onSavedJourneyClick(position: Int)
            fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int)
            fun editImageClicked(position: Int)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedJourneyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_saved_journeys_listitem,parent,false)
        return SavedJourneyViewHolder(view,viewHolderListener)
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

}