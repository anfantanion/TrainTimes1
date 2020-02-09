package com.anfantanion.traintimes1.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.repositories.StationRepo
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.FloatingSearchView.OnSearchListener
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mSearchView : FloatingSearchView

    private val TAG = "HomeFragment"

    private var callbacks : HomeFragmentCallbacks? = null

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
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSearchView = view.findViewById(R.id.home_floating_search_view)

        callbacks?.onAttachSearchViewToDrawer(mSearchView)
        activity?.actionBar?.hide()

        setupFloatingSearch()
    }

    private fun setupFloatingSearch(){
        mSearchView.setOnQueryChangeListener{ oldQuery: String, newQuery: String ->
            if (oldQuery != "" && newQuery == "") {
                mSearchView.clearSuggestions()
            }
            else {
                StationRepo.SearchManager.findSuggestions(newQuery,5, object : StationRepo.SearchManager.stationSuggestionListener {
                    override fun onResults(results: List<Station.StationSuggestion>) {
                        mSearchView.swapSuggestions(results)
                    }
                })

            }
            Log.d(TAG,"onQueryChange")
        }

        mSearchView.setOnSearchListener(object : OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                val station = StationRepo.SearchManager.getStation(searchSuggestion as Station.StationSuggestion)
                if (station!=null){
                    StationRepo.activeStation = station;
                    Log.d(TAG, "onSuggestionClicked()"+station.code)
                    findNavController().navigate(R.id.action_nav_home_to_stationDetails)
                    mSearchView.clearSearchFocus()
                }

            }

            override fun onSearchAction(query: String) {
                Log.d(TAG, "onSearchAction()")
                //TODO: Select top result
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
                R.id.action_settings -> navController?.navigate(R.id.nav_settings)
                R.id.action_voice_rec -> null //TODO: Voice
                R.id.action_location -> {
                    StationRepo.SearchManager.findNearby(object : StationRepo.SearchManager.stationSuggestionListener {
                        override fun onResults(results: List<Station.StationSuggestion>) {
                            mSearchView.swapSuggestions(results)
                        }
                    })
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).supportActionBar?.show()
    }
}