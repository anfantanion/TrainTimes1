package com.anfantanion.traintimes1.ui.activeJourney

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.databinding.FragmentActiveJourneyBinding
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.StationRepo
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ActiveJourneyFragment : Fragment(),
    View.OnClickListener,
    ActiveJourneyRecyclerAdapter.ActiveJourneyViewHolder.ViewHolderListener {

    private lateinit var activeJourneyViewModel: ActiveJourneyViewModel
    private lateinit var activeJourneyRecyclerAdapter: ActiveJourneyRecyclerAdapter

    private var _binding: FragmentActiveJourneyBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveJourneyBinding.inflate(inflater,container,false)
        activeJourneyViewModel = ViewModelProvider(this)[ActiveJourneyViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activeJourneyRecyclerAdapter = ActiveJourneyRecyclerAdapter(this)
        binding.activeJourneyServiceRecycler.layoutManager = LinearLayoutManager(context)
        binding.activeJourneyServiceRecycler.adapter = activeJourneyRecyclerAdapter

        activeJourneyViewModel.activeJourney.observe(viewLifecycleOwner) {
            when (it) {
                null -> {
                    binding.activeJourneyNoActiveJourney.visibility = View.VISIBLE
                }
                else -> {
                    binding.activeJourneyNoActiveJourney.visibility = View.GONE
                }
            }
            activeJourneyViewModel.getServices()
            activeJourneyRecyclerAdapter.waypoints =
                activeJourneyViewModel.getWaypointStations() ?: emptyList()
        }

        activeJourneyViewModel.refreshAge.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.activeJourneyInfo.visibility = View.VISIBLE
                binding.activeJourneyInfo.text =
                    requireContext().resources.getQuantityString(R.plurals.minutesTime, it, it)
            } else {
                binding.activeJourneyInfo.visibility = View.GONE
            }
        }

        activeJourneyViewModel.serviceResponses.observe(viewLifecycleOwner) {

            val change = activeJourneyViewModel.getNextChange()
            if (change!=null){
                binding.activeJourneyConnectionCardView.visibility = View.VISIBLE
                when(change.changeType){
                    ActiveJourney.KeyPoint.ChangeType.START -> {
                        binding.connectionCard.activeJourneyConnectionTitle2.text = getString(R.string.activeJourneyConnectionPlaceDepart, StationRepo.getStation(change.waypoint)!!.name)
                        binding.connectionCard.activeJourneyConnectionService1.visibility=View.GONE
                        binding.connectionCard.activeJourneyConnectionDeparting.visibility=View.VISIBLE
                        binding.connectionCard.activeJourneyService1.text = getString(R.string.activeJourneyService,"")
                        binding.connectionCard.activeJourneyService2.text = getString(R.string.activeJourneyService,"")
                        binding.connectionCard.activeJourneyService2Departs.text = getString(R.string.activeJourneyService2Departs,change.service1.getRTorTTDeparture(change.waypoint!!))
                        binding.connectionCard.activeJourneyService2Platform.text = getString(R.string.activeJourneyService2Platform,change.service1.getPlatform(change.waypoint)?: getString(R.string.UnknownPlat))
                    }

                    ActiveJourney.KeyPoint.ChangeType.CHANGE ->{
                        binding.connectionCard.activeJourneyConnectionTitle2.text = getString(R.string.activeJourneyConnectionPlaceChange, StationRepo.getStation(change.waypoint)!!.name)
                        binding.connectionCard.activeJourneyConnectionService1.visibility=View.VISIBLE
                        binding.connectionCard.activeJourneyConnectionDeparting.visibility=View.VISIBLE
                        binding.connectionCard.activeJourneyService1.text = getString(R.string.activeJourneyService,"1")
                        binding.connectionCard.activeJourneyService2.text = getString(R.string.activeJourneyService,"2")
                        binding.connectionCard.activeJourneyService1Arrives.text = getString(R.string.activeJourneyService1Arrives,change.service1.getRTorTTArrival(change.waypoint!!))
                        binding.connectionCard.activeJourneyService1Platform.text = getString(R.string.activeJourneyService1Platform,change.service1.getPlatform(change.waypoint)?: getString(R.string.UnknownPlat))
                        binding.connectionCard.activeJourneyService2Departs.text = getString(R.string.activeJourneyService2Departs,change.service2!!.getRTorTTDeparture(change.waypoint))
                        binding.connectionCard.activeJourneyService2Platform.text = getString(R.string.activeJourneyService2Platform,change.service2.getPlatform(change.waypoint)?: getString(R.string.UnknownPlat))
                    }

                    ActiveJourney.KeyPoint.ChangeType.END -> {
                        binding.connectionCard.activeJourneyConnectionTitle2.text = getString(R.string.activeJourneyConnectionPlaceArrive, StationRepo.getStation(change.waypoint)!!.name)
                        binding.connectionCard.activeJourneyConnectionService1.visibility=View.VISIBLE
                        binding.connectionCard.activeJourneyConnectionDeparting.visibility=View.GONE
                        binding.connectionCard.activeJourneyService1.text = getString(R.string.activeJourneyService,"")
                        binding.connectionCard.activeJourneyService2.text = getString(R.string.activeJourneyService,"")
                        binding.connectionCard.activeJourneyService1Arrives.text = getString(R.string.activeJourneyService1Arrives,change.service1.getRTorTTArrival(change.waypoint!!))
                        binding.connectionCard.activeJourneyService1Platform.text = getString(R.string.activeJourneyService2Platform,change.service1.getPlatform(change.waypoint)?: getString(R.string.UnknownPlat))
                    }
                }
            }else{
                binding.activeJourneyConnectionCardView.visibility = View.GONE
            }


            activeJourneyRecyclerAdapter.services = it
            activeJourneyRecyclerAdapter.notifyDataSetChanged()
        }

        activeJourneyViewModel.loadedPreviouslyPlannedRoute.observe(viewLifecycleOwner) {
            if (it)
                Toast.makeText(
                    context,
                    R.string.activeJourneyLoadedPreviousTrue,
                    Toast.LENGTH_SHORT
                ).show()
            else
                Toast.makeText(
                    context,
                    R.string.activeJourneyLoadedPreviousFalse,
                    Toast.LENGTH_SHORT
                ).show()
        }


        activeJourneyViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it)
                binding.activeJourneyProgressBar.visibility = View.VISIBLE
            else
                binding.activeJourneyProgressBar.visibility = View.GONE
        }

        activeJourneyViewModel.errorText.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext()).setMessage(it)
                .setPositiveButton(R.string.ok, null).show()
            // Toast.makeText(context,it,Toast.LENGTH_LONG).show()
        }

        binding.activeJourneySelectOrCreate.setOnClickListener(this)

        JourneyRepo.activeJourney.observe(viewLifecycleOwner) {
            activeJourneyViewModel.activeJourney.value = it
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_activejourney, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_activeJourneyEnd -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.activeJourneyEndTitle)
                    .setMessage(R.string.activeJourneyEndMessage)
                    .setPositiveButton(R.string.activeJourneyEndPositive) { _, _ ->
                        JourneyRepo.setActiveJourney(null)
                        activeJourneyViewModel.activeJourney.value = null
                    }
                    .setNegativeButton(R.string.activeJourneyEndNegative,null)
                    .show()
            }
            R.id.action_activeJourneyReplan -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.activeJourneyReplanTitle)
                    .setMessage(R.string.activeJourneyReplanhMessage)
                    .setPositiveButton(R.string.activeJourneyReplanPositive) { _, _ ->
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
            binding.activeJourneySelectOrCreate -> findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToNavSavedJourneys())
        }
    }

    override fun onItemClick(position: Int) {

    }

    override fun onDetailsButtonClick(position: Int) {
        val serviceStub = activeJourneyViewModel.serviceResponses.value!![position].toServiceStub()
        findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToServiceDetails(serviceStub))
    }

    override fun onMapButtonClick(position: Int) {
        val serviceStub = activeJourneyViewModel.serviceResponses.value!![position].toServiceStub()
        findNavController().navigate(ActiveJourneyFragmentDirections.actionNavActiveJourneyToMapsFragment(
            arrayOf(serviceStub)
        ))
    }

    override fun onExpandClick(position: Int) {

    }

    override fun onRefreshClick(position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.activeJourneyPartialRefreshTitle)
            .setMessage(R.string.activeJourneyPartialRefreshMessage)
            .setPositiveButton(R.string.activeJourneyPartialRefreshPositive) { _, _ ->
                activeJourneyViewModel.getServices(replanFrom = position)
            }
            .setNegativeButton(R.string.activeJourneyPartialRefreshNegative,null)
            .show()

    }

    override fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int) {

    }
}