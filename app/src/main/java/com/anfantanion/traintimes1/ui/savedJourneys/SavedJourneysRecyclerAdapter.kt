package com.anfantanion.traintimes1.ui.savedJourneys

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Journey
import kotlinx.android.synthetic.main.fragment_saved_journeys_listitem.view.*

class SavedJourneysRecyclerAdapter(
    var viewHolderListener: SavedJourneyViewHolder.ViewHolderListener
) : RecyclerView.Adapter<SavedJourneysRecyclerAdapter.SavedJourneyViewHolder>() {

    var journeys = emptyList<Journey>()
    var editMode = false

    class SavedJourneyViewHolder(itemView: View, private var viewHolderListener: ViewHolderListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnTouchListener{
        val title = itemView.savedJourneysListTitle!!
        val origin = itemView.savedJourneysListOrigin!!
        val to = itemView.savedJourneysListTo!!
        val dest = itemView.savedJourneysListDest!!
        val viaLayout = itemView.savedJourneysListViaLayout!!
        val viaLabel = itemView.savedJourneysListVia!!
        val viaStops = itemView.savedJourneysListViaStops!!
        val timeInfo = itemView.savedJourneysListTimeInfoLarge!!

        val editLayout = itemView.savedJourneysListEditLayout!!
        val editButton = itemView.savedJourneysListEditEdit!!
        val favButton = itemView.savedJourneysListEditFavourite!!
        val copyButton = itemView.savedJourneysListEditCopy!!
        val deleteButton = itemView.savedJourneysListEditDelete!!


        init {
            itemView.setOnClickListener(this)
            itemView.setOnTouchListener(this)
            editButton.setOnClickListener(this)
            copyButton.setOnClickListener(this)
            deleteButton.setOnClickListener(this)
            favButton.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            when (v){
                editButton -> viewHolderListener.onEditButtonClick(adapterPosition)
                copyButton -> viewHolderListener.onCopyButtonClick(adapterPosition)
                deleteButton -> viewHolderListener.onDeleteButtonClick(adapterPosition)
                favButton -> viewHolderListener.onFavButtonClick(adapterPosition)
                else -> viewHolderListener.onSavedJourneyClick(adapterPosition)
            }

        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                viewHolderListener.dragImageTouchDown(this,adapterPosition)
            }
            return false
        }

        interface ViewHolderListener{
            fun onSavedJourneyClick(position: Int)
            fun onEditButtonClick(position: Int)
            fun onFavButtonClick(position: Int)
            fun onCopyButtonClick(position: Int)
            fun onDeleteButtonClick(position: Int)
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
        val context = holder.itemView.context
        val journey = journeys[position]
        holder.title.text = journey.givenName ?: "Journey "+(position+1)
        holder.origin.text = journey.getOriginName()
        holder.dest.text = journey.getDestName()
        val via = journey.getIntermidateName()
        if (via == null){
            holder.viaLayout.visibility=View.INVISIBLE
        }else {
            holder.viaLayout.visibility=View.VISIBLE
            holder.viaStops.text = via
        }
        when(journey.type){
            Journey.Type.DYNAMIC -> holder.timeInfo.text = context.getString(R.string.savedJourneys_Dynamic)
            Journey.Type.DEPARTAT -> holder.timeInfo.text = context.getString(R.string.savedJourneys_DepartAt,journey.time)
            Journey.Type.ARRIVEBY -> holder.timeInfo.text = context.getString(R.string.savedJourneys_ArriveBy,journey.time)
        }
        if (editMode){
            holder.timeInfo.visibility = View.GONE
            holder.editLayout.visibility = View.VISIBLE
            if (journey.favourite){
                holder.favButton.setImageResource(R.drawable.ic_favorite_black_24dp)
            }else{
                holder.favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
        }else{
            holder.timeInfo.visibility = View.VISIBLE
            holder.editLayout.visibility = View.GONE
        }

    }

}