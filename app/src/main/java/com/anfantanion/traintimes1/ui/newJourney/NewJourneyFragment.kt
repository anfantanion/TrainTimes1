package com.anfantanion.traintimes1.ui.newJourney


import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Journey
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.StationRepo
import com.anfantanion.traintimes1.ui.stationDetails.SearchDialog
import kotlinx.android.synthetic.main.fragment_new_journey.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NewJourneyFragment : Fragment(),
    View.OnClickListener,
    NewJourneyRecyclerAdapter.ViewHolder.ViewHolderListener,
    NewJourneyRAITLCallbacks.NewJourneyRAItemTouchListener {

    private lateinit var newJourneyViewModel : NewJourneyViewModel
    private lateinit var newJourneyRecyclerAdapter: NewJourneyRecyclerAdapter
    private lateinit var newJourneyTouchHelper: ItemTouchHelper
    val args: NewJourneyFragmentArgs by navArgs()
    var editingJourney : Journey? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        editingJourney = JourneyRepo.getJourney(args.editingJourney)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_journey, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newJourneyViewModel = NewJourneyViewModel()

        newJourneyRecyclerAdapter = NewJourneyRecyclerAdapter(this,this)
        newJourneyRecyclerView.layoutManager = LinearLayoutManager(context)
        newJourneyRecyclerView.adapter = newJourneyRecyclerAdapter

        newJourneyDynamic.setOnClickListener(this)
        newJourneyArrive.setOnClickListener(this)
        newJourneyDepart.setOnClickListener(this)
        newJourneyArriveEdit.setOnClickListener(this)
        newJourneyDepartEdit.setOnClickListener(this)

        newJourneyViewModel.arriveTime.observe(viewLifecycleOwner, Observer{
            newJourneyArriveEdit.setText(it.getTime())
        })
        newJourneyViewModel.departTime.observe(viewLifecycleOwner, Observer{
            newJourneyDepartEdit.setText(it.getTime())
        })
        newJourneyViewModel.stations.observe(viewLifecycleOwner, Observer{
            newJourneyRecyclerAdapter.stations = it
            if (newJourneyViewModel.shouldUpdate)
                newJourneyRecyclerAdapter.notifyDataSetChanged()
        })

        newJourneyViewModel.stations.value = listOf(
            StationRepo.getStation(StationStub("AXM"))!!,
            StationRepo.getStation(StationStub("CLJ"))!!)


        val callbacks = NewJourneyRAITLCallbacks(this)
        newJourneyTouchHelper = ItemTouchHelper(callbacks)
        newJourneyTouchHelper.attachToRecyclerView(newJourneyRecyclerView)

    }


    override fun onResume() {
        super.onResume()
        if (editingJourney==null){
            (activity as MainActivity).supportActionBar?.title = getString(R.string.newJourneyAddMode)
        }else{
            (activity as MainActivity).supportActionBar?.title = getString(R.string.newJourneyEditMode)
        }
    }

    override fun onClick(v: View?) {
        when(v){
            newJourneyDynamic -> {
                uncheckAll()
                newJourneyDynamic.isChecked = true
            }
            newJourneyArrive -> {
                uncheckAll()
                newJourneyArrive.isChecked = true
                newJourneyArriveEdit.isEnabled = true
            }
            newJourneyDepart -> {
                uncheckAll()
                newJourneyDepart.isChecked = true
                newJourneyDepartEdit.isEnabled = true
            }
            newJourneyArriveEdit -> {
                TimePickerFragment(newJourneyViewModel.arriveTime).show(parentFragmentManager,"newJourneyTimePicker1")
            }
            newJourneyDepartEdit -> {
                TimePickerFragment(newJourneyViewModel.departTime).show(parentFragmentManager,"newJourneyTimePicker2")
            }
        }
    }



    private fun uncheckAll(){
        newJourneyDynamic.isChecked = false
        newJourneyArrive.isChecked = false
        newJourneyDepart.isChecked = false
        newJourneyDepartEdit.isEnabled = false
        newJourneyArriveEdit.isEnabled = false
    }

    override fun stationNameClicked(position: Int) {
        val sd = SearchDialog(object: SearchDialog.SearchDialogListener{
            override fun onItemSelected(stationSuggestion: Station.StationSuggestion) {
                StationRepo.SearchManager.addHistory(stationSuggestion)
                newJourneyViewModel.replaceStation(position,StationRepo.SearchManager.getStation(stationSuggestion)?: return)
            }
        })
        sd.show(parentFragmentManager,"searchDialogNewJourneyAdd")
    }

    override fun dragImageTouchDown(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        // If Reverse Button
        if (position == newJourneyViewModel.stations.value?.size ?: 0){
            newJourneyViewModel.reverseStations()
        }else {
            newJourneyTouchHelper.startDrag(viewHolder)
        }
    }

    override fun delImageClicked(position: Int) {
        newJourneyViewModel.removeStation(position)
    }

    override fun addImageClicked() {
        val sd = SearchDialog(object: SearchDialog.SearchDialogListener{
            override fun onItemSelected(stationSuggestion: Station.StationSuggestion) {
                StationRepo.SearchManager.addHistory(stationSuggestion)
                newJourneyViewModel.addStation(StationRepo.SearchManager.getStation(stationSuggestion)?: return)
            }
        })
        sd.show(parentFragmentManager,"searchDialogNewJourneyAdd")
    }

    override fun onMove(start: Int, end: Int) {
        val noStations = newJourneyViewModel.stations.value?.size ?: 0
        val end2 =
            if (end+1 >= noStations )
                noStations - 1
            else
                end


        newJourneyViewModel.shouldUpdate = false
        newJourneyViewModel.swapStations(start,end2)
        newJourneyRecyclerAdapter.notifyItemMoved(start,end2)
        newJourneyViewModel.shouldUpdate = true
    }

    override fun onSwipe(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun noStations(): Int {
        return newJourneyViewModel.stations.value!!.size
    }


    class TimePickerFragment(private val calendarToUpdate: MutableLiveData<TimeDate>) : DialogFragment(),
        TimePickerDialog.OnTimeSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return TimePickerDialog(
                context!!,
                this,
                calendarToUpdate.value!!.calendar.get(Calendar.HOUR_OF_DAY),
                calendarToUpdate.value!!.calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(context!!)
            )
        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            val timeDate = calendarToUpdate.value!!
            timeDate.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            timeDate.calendar.set(Calendar.MINUTE, minute)
            calendarToUpdate.value = timeDate
        }


    }


}
