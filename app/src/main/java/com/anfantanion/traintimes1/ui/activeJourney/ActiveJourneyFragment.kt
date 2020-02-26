package com.anfantanion.traintimes1.ui.activeJourney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import kotlinx.android.synthetic.main.fragment_active_journey.*
import kotlinx.android.synthetic.main.fragment_active_journey_listitem.*

class ActiveJourneyFragment : Fragment(),
    View.OnClickListener,
    ActiveJourneyRecyclerAdapter.ActiveJourneyViewHolder.ViewHolderListener {

    private lateinit var activeJourneyViewModel: ActiveJourneyViewModel
    private lateinit var activeJourneyRecyclerAdapter: ActiveJourneyRecyclerAdapter
    private lateinit var savedJourneyTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activeJourneyViewModel = ViewModelProvider(this).get(ActiveJourneyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_active_journey, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activeJourneyRecyclerAdapter = ActiveJourneyRecyclerAdapter(this)
        activeJourneyServiceRecycler.layoutManager = LinearLayoutManager(context)
        activeJourneyServiceRecycler.adapter = activeJourneyRecyclerAdapter

        activeJourneyViewModel.activeJourney.observe(viewLifecycleOwner, Observer {
            when(it){
                null -> {
                    activeJourneyNoActiveJourney.visibility = View.VISIBLE
                }
                else -> {
                    activeJourneyNoActiveJourney.visibility = View.GONE
                }
            }
            activeJourneyViewModel.getServices()
        })

        activeJourneyViewModel.serviceResponses.observe(viewLifecycleOwner, Observer {
            if (it.size>1){// If number of services is greater than 1, show connection Info
                activeJourneyConnectionCardView.visibility = View.VISIBLE
            }
        })


        activeJourneySelectOrCreate.setOnClickListener(this)







    }

    override fun onClick(v: View?) {
        when (v){
            activeJourneySelectOrCreate -> findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToNavSavedJourneys())
        }
    }

    override fun onItemJourneyClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDetailsButtonClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMapButtonClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}