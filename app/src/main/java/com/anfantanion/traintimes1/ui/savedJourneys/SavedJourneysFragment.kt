package com.anfantanion.traintimes1.ui.savedJourneys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anfantanion.traintimes1.R
import kotlinx.android.synthetic.main.fragment_saved_journeys.*

class SavedJourneysFragment : Fragment(), SavedJourneyRecyclerAdapter.SavedJourneyRecycClick {

    private lateinit var savedJourneysViewModel: SavedJourneysViewModel
    private lateinit var savedJourneyRecyclerAdapter: SavedJourneyRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        savedJourneysViewModel = ViewModelProvider(this).get(SavedJourneysViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_saved_journeys, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedJourneyRecyclerAdapter = SavedJourneyRecyclerAdapter(this)
        savedJourneyRecyclerAdapter.journeys = savedJourneysViewModel.journeys.value ?: emptyList()

        savedJourneysRecyclerView.layoutManager = LinearLayoutManager(context)
        savedJourneysRecyclerView.adapter = savedJourneyRecyclerAdapter

        savedJourneysViewModel.journeys.observe(viewLifecycleOwner, Observer{
            savedJourneyRecyclerAdapter.journeys = it ?: emptyList()
        })

        savedJourneysViewModel.getJourneys()





    }

    override fun onSavedJourneyClick(position: Int) {
        val clicked = savedJourneysViewModel.journeys.value!!.get(position)
    }
}