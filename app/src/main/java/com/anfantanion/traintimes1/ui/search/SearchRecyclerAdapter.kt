package com.anfantanion.traintimes1.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station

class SearchRecyclerAdapter() : RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>(), Filterable  {
    val TAG = "SearchRecyclerAdapter"

    var stations = emptyList<Station>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.SearchImage)
        val line1 = itemView.findViewById<TextView>(R.id.SearchLine1)
        val line2 = itemView.findViewById<TextView>(R.id.SearchLine2)
        val relativeLayout = itemView.findViewById<RelativeLayout>(R.id.SearchRL)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_search_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG,"onBindViewHolder: called")
        holder.line1.text = stations[position].name
        holder.line2.text = stations[position].code

    }

    override fun getFilter(): Filter {
        return CustomFilter
    }

    object CustomFilter : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}