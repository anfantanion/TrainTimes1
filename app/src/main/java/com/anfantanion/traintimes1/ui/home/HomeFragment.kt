package com.anfantanion.traintimes1.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.notify.NotifyManager
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.StationRepo
import com.anfantanion.traintimes1.ui.savedJourneys.SavedJourneysRecyclerAdapter
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.FloatingSearchView.OnSearchListener
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.connection_card.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(),
    SavedJourneysRecyclerAdapter.SavedJourneyViewHolder.ViewHolderListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mSearchView : FloatingSearchView

    private val TAG = "HomeFragment"

    private var callbacks : HomeFragmentCallbacks? = null
    private lateinit var savedJourneysRecyclerAdapter : SavedJourneysRecyclerAdapter

    interface HomeFragmentCallbacks {
        fun onAttachSearchViewToDrawer(searchView: FloatingSearchView)
        fun hideActionBar()
        fun showActionBar()
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentCallbacks) {
            callbacks = context
        }else {
            throw RuntimeException(
                context.toString()
                        + " must implement BaseExampleFragmentCallbacks"
            )
        }
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSearchView = view.findViewById(R.id.home_floating_search_view)



        val imageView = view.findViewById<ImageView>(R.id.home_background)


        setupFloatingSearch()

        homeViewModel.favouriteJourneys.observe(viewLifecycleOwner, Observer {
            savedJourneysRecyclerAdapter.journeys = it
        })

        homeViewModel.activeJourney.observe(viewLifecycleOwner, Observer {
            val change = it?.getCurrentKeyPoint()
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
        })

        homeConnectionDetails.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavActiveJourney())
        }

        homeFavouriteJourneyCard.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSavedJourneys())
        }

        homeSavedJourneyCard.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavSavedJourneys())
        }

        homeMapCard.setOnClickListener{
            Toast.makeText(context,"SEnding in 10 sec",Toast.LENGTH_SHORT).show()
            NotifyManager.sendNotificationIn(10)
        }

        savedJourneysRecyclerAdapter = SavedJourneysRecyclerAdapter(this)


        homeFavouriteJourney.layoutManager = LinearLayoutManager(context)
        homeFavouriteJourney.adapter = savedJourneysRecyclerAdapter

        homeViewModel.getFavourites()

        //callbacks?.onAttachSearchViewToDrawer(mSearchView)
    }

    override fun onStart() {
        super.onStart()
        callbacks?.onAttachSearchViewToDrawer(mSearchView)
    }

    private fun setupFloatingSearch(){
        mSearchView.setOnQueryChangeListener{ oldQuery: String, newQuery: String ->
            if (oldQuery != "" && newQuery == "") {
                mSearchView.clearSuggestions()
            }
            else {
                StationRepo.SearchManager.findSuggestions(newQuery,5, object : StationRepo.SearchManager.StationSuggestionListener {
                    override fun onResults(results: List<Station.StationSuggestion>) {
                        mSearchView.swapSuggestions(results)
                    }
                    override fun onError(reason: StationRepo.SearchManager.LocationError) {
                    }
                })

            }
            Log.d(TAG,"onQueryChange")
        }

        mSearchView.setOnSearchListener(object : OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                val station = StationRepo.SearchManager.getStation(searchSuggestion as Station.StationSuggestion)
                if (station!=null){
                    Log.d(TAG, "onSuggestionClicked()"+station.code)
                    StationRepo.SearchManager.addHistory(searchSuggestion)
                    findNavController().navigate(HomeFragmentDirections.actionNavHomeToStationDetails(station.toStationStub()))
                    mSearchView.clearSearchFocus()
                }

            }

            override fun onSearchAction(query: String) {
                val station = StationRepo.SearchManager.getStation(StationRepo.SearchManager.lastSearchTopStationSuggestion as Station.StationSuggestion)
                if (station!=null){
                    Log.d(TAG, "onSearchAction()"+station.code)
                    StationRepo.SearchManager.addHistory(station.getStationSuggestion())
                    findNavController().navigate(HomeFragmentDirections.actionNavHomeToStationDetails(station.toStationStub()))
                    mSearchView.clearSearchFocus()
                }
            }
        })

        mSearchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() { //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(StationRepo.SearchManager.getHistory(3))
                Log.d(TAG, "onFocus()")
            }

            override fun onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle("")
                Log.d(TAG, "onFocusCleared()")
            }
        })

        mSearchView.setOnMenuItemClickListener { item ->
            val navController = activity?.findNavController(R.id.nav_host_fragment)
            when (item?.itemId) {
                R.id.action_settings -> navController?.navigate(R.id.action_nav_home_to_nav_settings)
                R.id.action_voice_rec -> null //TODO: Voice
                R.id.action_location -> {
                    mSearchView.requestFocus()
                    mSearchView.setSearchFocused(true)
                    StationRepo.SearchManager.findNearby(object : StationRepo.SearchManager.StationSuggestionListener {
                        override fun onResults(results: List<Station.StationSuggestion>) {
                            mSearchView.swapSuggestions(results)
                        }
                        override fun onError(reason: StationRepo.SearchManager.LocationError) {
                            Toast.makeText(context,R.string.noLocation, Toast.LENGTH_SHORT).show()
                        }
                    }, 3)
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.hide()
        (activity as MainActivity).window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).supportActionBar?.show()
        (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    override fun onSavedJourneyClick(position: Int) {
        val clicked = homeViewModel.favouriteJourneys.value!!.get(position)
        if (JourneyRepo.activeJourney.value != null){

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.savedJourneys_Dialog_OverwriteTitle)
                .setMessage(R.string.savedJourneys_Dialog_OverwriteMessage)
                .setPositiveButton(R.string.savedJourneys_Dialog_OverwritePositive) { dialog, id ->
                    JourneyRepo.setActiveJourney(clicked.getActiveJourneyCopy())
                    findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavActiveJourney())
                }
                .setNegativeButton(R.string.savedJourneys_Dialog_OverwriteNegative,null)
                .show()
        }else{
            JourneyRepo.setActiveJourney(clicked.getActiveJourneyCopy())
            findNavController().navigate(HomeFragmentDirections.actionNavHomeToNavActiveJourney())
        }

    }

    override fun onEditButtonClick(position: Int) {
    }

    override fun onFavButtonClick(position: Int) {
    }

    override fun onCopyButtonClick(position: Int) {
    }

    override fun onDeleteButtonClick(position: Int) {
    }

    override fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun editImageClicked(position: Int) {
    }
}