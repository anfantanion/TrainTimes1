package com.anfantanion.traintimes1.ui.savedJourneys

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.ui.common.ItemTouchHelperCallbacks
import kotlinx.android.synthetic.main.fragment_saved_journeys.*

class SavedJourneysFragment :
        Fragment(),
        SavedJourneysRecyclerAdapter.SavedJourneyViewHolder.ViewHolderListener,
        ItemTouchHelperCallbacks.ItemTouchHelperListener,
        View.OnClickListener

{

    private lateinit var savedJourneysViewModel: SavedJourneysViewModel
    private lateinit var savedJourneysRecyclerAdapter: SavedJourneysRecyclerAdapter
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
        savedJourneysRecyclerAdapter = SavedJourneysRecyclerAdapter(this)
        savedJourneysRecyclerAdapter.journeys = savedJourneysViewModel.journeys.value ?: emptyList()

        savedJourneysRecyclerView.layoutManager = LinearLayoutManager(context)
        savedJourneysRecyclerView.adapter = savedJourneysRecyclerAdapter

        savedJourneysViewModel.journeys.observe(viewLifecycleOwner, Observer{
            savedJourneysRecyclerAdapter.journeys = it ?: emptyList()
            if (savedJourneysViewModel.doUpdate)
                savedJourneysRecyclerAdapter.notifyDataSetChanged()
        })

        savedJourneysViewModel.journeys.observe(viewLifecycleOwner, Observer{
            savedJourneysRecyclerAdapter.journeys = it ?: emptyList()
            if (savedJourneysViewModel.doUpdate)
                savedJourneysRecyclerAdapter.notifyDataSetChanged()
        })

        savedJourneysViewModel.editMode.observe(viewLifecycleOwner, Observer {
            savedJourneysRecyclerAdapter.editMode = it
            savedJourneysRecyclerAdapter.notifyDataSetChanged()
        })

        savedJourneyAddButton.setOnClickListener(this)

        val callbacks = ItemTouchHelperCallbacks(this)

        savedJourneyTouchHelper = ItemTouchHelper(callbacks)
        savedJourneyTouchHelper.attachToRecyclerView(savedJourneysRecyclerView)

        savedJourneysViewModel.getJourneys()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_savedjourneys, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onClick(v: View?) {
        when(v){
            savedJourneyAddButton -> findNavController().navigate(SavedJourneysFragmentDirections.actionNavSavedJourneysToNewJourneyFragment(null))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_savedJourneys_edit -> {
                if (savedJourneysViewModel.toggleEdit())
                    Toast.makeText(context,R.string.savedJourneys_EditModeOn,Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context,R.string.savedJourneys_EditModeOff,Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSavedJourneyClick(position: Int) {
        val clicked = savedJourneysViewModel.journeys.value!!.get(position)
        if (JourneyRepo.activeJourney != null){

            AlertDialog.Builder(context)
                .setTitle(R.string.savedJourneys_Dialog_OverwriteTitle)
                .setMessage(R.string.savedJourneys_Dialog_OverwriteMessage)
                .setPositiveButton(R.string.savedJourneys_Dialog_OverwritePositive) { dialog, id ->
                    JourneyRepo.activeJourney = clicked.getActiveJourneyCopy()
                    findNavController().navigate(SavedJourneysFragmentDirections.actionNavSavedJourneysToNavActiveJourney())
                }
                .setNegativeButton(R.string.savedJourneys_Dialog_OverwriteNegative,null)
                .show()
        }else{
            JourneyRepo.activeJourney = clicked.getActiveJourneyCopy()
            findNavController().navigate(SavedJourneysFragmentDirections.actionNavSavedJourneysToNavActiveJourney())
        }

    }

    override fun onEditButtonClick(position: Int) {
        val clicked = savedJourneysViewModel.journeys.value!!.get(position)
        findNavController().navigate(SavedJourneysFragmentDirections.actionNavSavedJourneysToNewJourneyFragment(clicked.toJourneyStub()))
    }

    override fun onCopyButtonClick(position: Int) {
        val clicked = savedJourneysViewModel.journeys.value!!.get(position)
        val newJourney = savedJourneysViewModel.copyJourney(clicked)
        findNavController().navigate(SavedJourneysFragmentDirections.actionNavSavedJourneysToNewJourneyFragment(newJourney.toJourneyStub()))
    }

    override fun onDeleteButtonClick(position: Int) {
        val clicked = savedJourneysViewModel.journeys.value!!.get(position)
        savedJourneysViewModel.removeJourney(clicked)
    }

    override fun dragImageTouchDown(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (savedJourneysViewModel.editMode.value!!)
            savedJourneyTouchHelper.startDrag(viewHolder)
    }

    override fun editImageClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMove(start: Int, end: Int) {
        savedJourneysViewModel.swapJourneys(start,end)
        savedJourneysRecyclerAdapter.notifyItemMoved(start,end)
    }

    override fun onSwipe(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}