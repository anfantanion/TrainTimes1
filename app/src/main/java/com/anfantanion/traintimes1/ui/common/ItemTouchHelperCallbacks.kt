package com.anfantanion.traintimes1.ui.common

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallbacks(private val itemTouchListener: ItemTouchHelperListener) : ItemTouchHelper.Callback(){

    var itemViewSwipeEnabled = false
    var longPressDragEnabled = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END
        )
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
        return itemViewSwipeEnabled
    }

    override fun isLongPressDragEnabled(): Boolean {
        return longPressDragEnabled
    }

    interface ItemTouchHelperListener{
        fun onMove(start: Int, end: Int)
        fun onSwipe(position: Int)
    }

}