package com.anfantanion.traintimes1.ui.newJourney

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.repositories.StationRepo
import kotlinx.android.synthetic.main.fragment_new_journey_listitem.view.*


class NewJourneyRecyclerAdapter(
    var viewHolderListener: ViewHolder.ViewHolderListener,
    var newJourneyRAItemTouchListener: NewJourneyRAITLCallbacks.NewJourneyRAItemTouchListener
) : RecyclerView.Adapter<NewJourneyRecyclerAdapter.ViewHolder>()  {



    var stations = emptyList<Station>()

    class ViewHolder(itemView: View, var viewHolderListener: ViewHolderListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnTouchListener{


        var stationName = itemView.newJourneyListItemName
        var dragImage = itemView.newJourneyListItemImageDragger
        var delImage = itemView.newJourneyListItemImageRemove
        var addImage = itemView.newJourneyListItemImageAdd

        init {
            stationName.setOnClickListener(this)
            //dragImage.setOnClickListener(this)
            dragImage.setOnTouchListener(this)
            delImage.setOnClickListener(this)
            addImage.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v){
                stationName -> viewHolderListener.stationNameClicked(adapterPosition)
                dragImage -> viewHolderListener.dragImageClicked(this)
                delImage -> viewHolderListener.delImageClicked(adapterPosition)
                addImage -> viewHolderListener.addImageClicked()
            }
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            if (event?.action == MotionEvent.ACTION_DOWN) {
                viewHolderListener.dragImageClicked(this)
            }
            return false
        }

        interface ViewHolderListener{
            fun stationNameClicked(position: Int)
            fun dragImageClicked(viewHolder: RecyclerView.ViewHolder)
            fun delImageClicked(position: Int)
            fun addImageClicked()
        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_new_journey_listitem,parent,false)
        return ViewHolder(view,viewHolderListener)
    }

    override fun getItemCount(): Int {
        return stations.size+1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == stations.size){
            holder.dragImage.visibility = View.INVISIBLE
            holder.delImage.visibility = View.INVISIBLE
            holder.addImage.visibility = View.VISIBLE
            holder.stationName.visibility= View.GONE
        }else {
            holder.dragImage.visibility = View.VISIBLE
            holder.delImage.visibility = View.VISIBLE
            holder.addImage.visibility = View.GONE
            holder.stationName.visibility= View.VISIBLE
            holder.stationName.text = stations[position].name
        }
    }

}

class NewJourneyRAITLCallbacks(val itemTouchListener: NewJourneyRAItemTouchListener) : ItemTouchHelper.Callback(){

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        var dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        if (viewHolder.adapterPosition == 0)
            dragFlag = dragFlag xor ItemTouchHelper.UP
        if (viewHolder.adapterPosition >= itemTouchListener.noStations())
            dragFlag = dragFlag xor ItemTouchHelper.DOWN

        return makeMovementFlags(
            dragFlag,
            ItemTouchHelper.START or ItemTouchHelper.END)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        itemTouchListener.onMove(viewHolder.adapterPosition,target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    interface NewJourneyRAItemTouchListener{
        fun onMove(start: Int, end: Int)
        fun onSwipe(position: Int)
        fun noStations() : Int
    }

}