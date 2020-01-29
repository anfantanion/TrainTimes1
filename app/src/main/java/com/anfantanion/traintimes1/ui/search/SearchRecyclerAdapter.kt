package com.anfantanion.traintimes1.ui.search

import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import kotlinx.android.synthetic.main.fragment_search_listitem.view.*

class SearchRecyclerAdapter(var mContext : Context, var stations : List<Station>) : RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {
    val TAG = "SearchRecyclerAdapter"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.SearchImage)
        val line1 = itemView.findViewById<TextView>(R.id.SearchLine1)
        val line2 = itemView.findViewById<TextView>(R.id.SearchLine2)
        val relativeLayout = itemView.findViewById<RelativeLayout>(R.id.SearchRL)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.fragment_search_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG,"onBindViewHolder: called")
        holder.line1.text = stations[position].name
        holder.line2.text = stations[position].abreviation
        holder.relativeLayout.setOnClickListener {
            Log.d(TAG,"onClick")
            Toast.makeText(mContext,stations[position].abreviation,Toast.LENGTH_SHORT).show()
        }

    }


}