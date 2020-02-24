package com.anfantanion.traintimes1.ui.savedJourneys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.ui.common.ItemTouchHelperCallbacks
import kotlinx.android.synthetic.main.fragment_new_journey.*
import kotlinx.android.synthetic.main.fragment_saved_journeys.*

class SavedJourneysFragment : Fragment(), SavedJourneyRecyclerAdapter.SavedJourneyViewHolder.ViewHolderListener, ItemTouchHelperCallbacks.NewJourneyRAItemTouchListener {

    private lateinit var savedJourneysViewModel: SavedJourneysViewModel
    private lateinit var savedJourneyRecyclerAdapter: SavedJourneyRecyclerAdapter
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
            if (savedJourneysViewModel.doUpdate)
                savedJourneyRecyclerAdapter.notifyDataSetChanged()
        })

        savedJourneysViewModel.getJourneys()


        savedJourneyAddButton.setOnClickListener {
            findNavController().navigate(SavedJourneysFragmentDirections.actionNavSavedJourneysToNewJourneyFragment(null))
        }

        val callbacks =
            ItemTouchHelperCallbacks(this)
        savedJourneyTouchHelper = ItemTouchHelper(callbacks)
        savedJourneyTouchHelper.attachToRecyclerView(savedJourneysRecyclerView)





    }

    override fun onSavedJourneyClick(position: Int) {
        val clicked = savedJourneysViewModel.journeys.value!!.get(position)
        findNavController().navigate(SavedJourneysFragmentDirections.actionNavSavedJourneysToNavActiveJourney())
    }

    override fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int) {
        savedJourneyTouchHelper.startDrag(viewHolder)
    }

    override fun editImageClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMove(start: Int, end: Int) {
        savedJourneysViewModel.swapJourneys(start,end)
        savedJourneyRecyclerAdapter.notifyItemMoved(start,end)
    }

    override fun onSwipe(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}