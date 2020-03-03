package com.anfantanion.traintimes1.ui.activeJourney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.repositories.StationRepo
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
            activeJourneyRecyclerAdapter.waypoints = activeJourneyViewModel.getWaypointStations() ?: emptyList()
        })

        activeJourneyViewModel.serviceResponses.observe(viewLifecycleOwner, Observer {
            if (it.size>1){// If number of services is greater than 1, show connection Info

                var change = activeJourneyViewModel.getNextChange()
                if (change!=null){
                    activeJourneyConnectionCardView.visibility = View.VISIBLE
                    activeJourneyConnectionTitle2.text = getString(R.string.activeJourneyConnectionPlace,
                        StationRepo.getStation(change.waypoint)!!.name)
                    //activeJourneyService1.text = getString(R.string.activeJourneyService1)
                    activeJourneyService1Arrives.text = getString(R.string.activeJourneyService1Arrives,change.service1.getRTStationArrival(change.waypoint))
                    activeJourneyService1Platform.text = getString(R.string.activeJourneyService1Platform,change.service1.getPlatform(change.waypoint))
                    activeJourneyService2Departs.text = getString(R.string.activeJourneyService2Departs,change.service2.getRTStationDeparture(change.waypoint))
                    activeJourneyService2Platform.text = getString(R.string.activeJourneyService2Platform,change.service2.getPlatform(change.waypoint))
                }


            }else activeJourneyConnectionCardView.visibility = View.VISIBLE
            activeJourneyRecyclerAdapter.services = it
            activeJourneyRecyclerAdapter.notifyDataSetChanged()
        })

        activeJourneyViewModel.loadedPreviouslyPlannedRoute.observe(viewLifecycleOwner, Observer {
            if (it)
                Toast.makeText(context,R.string.activeJourneyLoadedPreviousTrue,Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context,R.string.activeJourneyLoadedPreviousFalse,Toast.LENGTH_SHORT).show()
        })


        activeJourneyViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                activeJourneyProgressBar.visibility = View.VISIBLE
            else
                activeJourneyProgressBar.visibility = View.GONE
        })

        activeJourneyViewModel.errorText.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context,it,Toast.LENGTH_LONG).show()
        })

        activeJourneySelectOrCreate.setOnClickListener(this)







    }

    override fun onClick(v: View?) {
        when (v){
            activeJourneySelectOrCreate -> findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToNavSavedJourneys())
        }
    }

    override fun onItemJourneyClick(position: Int) {

    }

    override fun onDetailsButtonClick(position: Int) {
        var serviceStub = activeJourneyViewModel.serviceResponses.value!!.get(position).toServiceStub()
        findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToServiceDetails(serviceStub))
    }

    override fun onMapButtonClick(position: Int) {

    }

    override fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int) {

    }
}