package com.anfantanion.traintimes1.ui.activeJourney

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.StationRepo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.connection_card.*
import kotlinx.android.synthetic.main.fragment_active_journey.*

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

        activeJourneyViewModel.refreshAge.observe(viewLifecycleOwner, Observer {
            if (it!= null){
                activeJourneyInfo.visibility = View.VISIBLE
                activeJourneyInfo.text = context!!.resources.getQuantityString(R.plurals.minutesTime,it,it)
            }
            else {
                activeJourneyInfo.visibility = View.GONE
            }
        })

        activeJourneyViewModel.serviceResponses.observe(viewLifecycleOwner, Observer {

            var change = activeJourneyViewModel.getNextChange()
            if (change!=null){
                activeJourneyConnectionCardView.visibility = View.VISIBLE
                when(change.changeType){
                    ActiveJourney.KeyPoint.ChangeType.START -> {
                        activeJourneyConnectionTitle2.text = getString(R.string.activeJourneyConnectionPlaceDepart, StationRepo.getStation(change.waypoint)!!.name)
                        activeJourneyConnectionService1.visibility=View.GONE
                        activeJourneyConnectionDeparting.visibility=View.VISIBLE
                        activeJourneyService1.text = getString(R.string.activeJourneyService,"")
                        activeJourneyService2.text = getString(R.string.activeJourneyService,"")
                        activeJourneyService2Departs.text = getString(R.string.activeJourneyService2Departs,change.service1.getRTorTTDeparture(change.waypoint!!))
                        activeJourneyService2Platform.text = getString(R.string.activeJourneyService2Platform,change.service1.getPlatform(change.waypoint!!)?: getString(R.string.UnknownPlat))
                    }

                    ActiveJourney.KeyPoint.ChangeType.CHANGE ->{
                        activeJourneyConnectionTitle2.text = getString(R.string.activeJourneyConnectionPlaceChange, StationRepo.getStation(change.waypoint)!!.name)
                        activeJourneyConnectionService1.visibility=View.VISIBLE
                        activeJourneyConnectionDeparting.visibility=View.VISIBLE
                        activeJourneyService1.text = getString(R.string.activeJourneyService,"1")
                        activeJourneyService2.text = getString(R.string.activeJourneyService,"2")
                        activeJourneyService1Arrives.text = getString(R.string.activeJourneyService1Arrives,change.service1.getRTorTTArrival(change.waypoint!!))
                        activeJourneyService1Platform.text = getString(R.string.activeJourneyService1Platform,change.service1.getPlatform(change.waypoint!!)?: getString(R.string.UnknownPlat))
                        activeJourneyService2Departs.text = getString(R.string.activeJourneyService2Departs,change.service2!!.getRTorTTDeparture(change.waypoint!!))
                        activeJourneyService2Platform.text = getString(R.string.activeJourneyService2Platform,change.service2!!.getPlatform(change.waypoint!!)?: getString(R.string.UnknownPlat))
                    }

                    ActiveJourney.KeyPoint.ChangeType.END -> {
                        activeJourneyConnectionTitle2.text = getString(R.string.activeJourneyConnectionPlaceArrive, StationRepo.getStation(change.waypoint)!!.name)
                        activeJourneyConnectionService1.visibility=View.VISIBLE
                        activeJourneyConnectionDeparting.visibility=View.GONE
                        activeJourneyService1.text = getString(R.string.activeJourneyService,"")
                        activeJourneyService2.text = getString(R.string.activeJourneyService,"")
                        activeJourneyService1Arrives.text = getString(R.string.activeJourneyService1Arrives,change.service1.getRTorTTArrival(change.waypoint!!))
                        activeJourneyService1Platform.text = getString(R.string.activeJourneyService2Platform,change.service1.getPlatform(change.waypoint!!)?: getString(R.string.UnknownPlat))
                    }

                }
            }else{
                activeJourneyConnectionCardView.visibility = View.GONE
            }


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
            MaterialAlertDialogBuilder(context).setMessage(it).setPositiveButton(R.string.ok,null).show()
           // Toast.makeText(context,it,Toast.LENGTH_LONG).show()
        })

        activeJourneySelectOrCreate.setOnClickListener(this)

        JourneyRepo.activeJourney.observe(viewLifecycleOwner, Observer{
            activeJourneyViewModel.activeJourney.value = it
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_activejourney, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_activeJourneyEnd -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.activeJourneyEndTitle)
                    .setMessage(R.string.activeJourneyEndMessage)
                    .setPositiveButton(R.string.activeJourneyEndPositive) { dialog, id ->
                        JourneyRepo.setActiveJourney(null)
                        activeJourneyViewModel.activeJourney.value = null
                    }
                    .setNegativeButton(R.string.activeJourneyEndNegative,null)
                    .show()
            }
            R.id.action_activeJourneyReplan -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.activeJourneyReplanTitle)
                    .setMessage(R.string.activeJourneyReplanhMessage)
                    .setPositiveButton(R.string.activeJourneyReplanPositive) { dialog, id ->
                        activeJourneyViewModel.getServices(_forceReplan = true)
                    }
                    .setNegativeButton(R.string.activeJourneyReplanNegative,null)
                    .show()
            }
            R.id.action_activeJourneyRefresh -> {
                activeJourneyViewModel.getServices()
            }
            R.id.action_activeJourney_onMap -> {
                findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToMapsFragment(
                    activeJourneyViewModel.serviceResponses.value!!.map { x -> x.toServiceStub() }.toTypedArray(),
                    activeJourneyViewModel.getCurrentServiceNo()?:0
                ))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v){
            activeJourneySelectOrCreate -> findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToNavSavedJourneys())
        }
    }

    override fun onItemClick(position: Int) {

    }

    override fun onDetailsButtonClick(position: Int) {
        var serviceStub = activeJourneyViewModel.serviceResponses.value!!.get(position).toServiceStub()
        findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToServiceDetails(serviceStub))
    }

    override fun onMapButtonClick(position: Int) {
        val serviceStub = activeJourneyViewModel.serviceResponses.value!!.get(position).toServiceStub()
        findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToMapsFragment(
            arrayOf(serviceStub)
        ))
    }

    override fun onExpandClick(position: Int) {

    }

    override fun onRefreshClick(position: Int) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.activeJourneyPartialRefreshTitle)
            .setMessage(R.string.activeJourneyPartialRefreshMessage)
            .setPositiveButton(R.string.activeJourneyPartialRefreshPositive) { dialog, id ->
                activeJourneyViewModel.getServices(replanFrom = position)
            }
            .setNegativeButton(R.string.activeJourneyPartialRefreshNegative,null)
            .show()

    }

    override fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int) {

    }
}