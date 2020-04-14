package com.anfantanion.traintimes1.ui.stationDetails

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.repositories.StationRepo
import kotlinx.android.synthetic.main.fragment_station_details.*


/**
 *
 */
class StationDetailsFragment : Fragment(), StationDetailsRecylerAdapter.OnServiceClick {
    private val TAG = "StationDetails"


    lateinit var viewModel: StationDetailsViewModel
    private lateinit var stationDetailsRecylerAdapter: StationDetailsRecylerAdapter

    private var receivedStation : Station? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        receivedStation = StationRepo.getStation(arguments!!.getParcelable("ActiveStation"))
        Log.d(TAG,receivedStation!!.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(StationDetailsViewModel::class.java)


        stationDetailsRecylerAdapter = StationDetailsRecylerAdapter(this)



        stationDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        stationDetailsRecyclerView.adapter = stationDetailsRecylerAdapter

        viewModel.stationResponse.observe(viewLifecycleOwner, Observer {
            stationDetailsRecylerAdapter.services = viewModel.stationResponse.value!!.services ?: emptyList()
            if (stationDetailsRecylerAdapter.services.isEmpty()){
                stationDetailsEmpty.visibility=View.VISIBLE
            }else {
                stationDetailsEmpty.visibility=View.GONE
            }
            stationDetailsRecylerAdapter.notifyDataSetChanged()
        })

        viewModel.isLoading.observe(viewLifecycleOwner,Observer{
                b -> when(b){
            true -> stationDetailsProgressBar.visibility = View.VISIBLE
            false -> stationDetailsProgressBar.visibility = View.GONE
            }
        })

        viewModel.isError.observe(viewLifecycleOwner,Observer{
                b -> when(b){
            true -> Toast.makeText(context,"Error "+viewModel.lastError.toString(),Toast.LENGTH_LONG).show()
            }
        })

        viewModel.isFiltered.observe(viewLifecycleOwner,Observer{
                b -> when(b){
            true -> {
                stationDetailsFilterInfo.visibility = View.VISIBLE
                stationDetailsFilterInfo.text=viewModel.filterToString()
            }
            false -> stationDetailsFilterInfo.visibility=View.GONE
        }
        })




        viewModel.station = receivedStation
        viewModel.getServices()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_stationdetails, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_stationDetails_refresh -> viewModel.getServices()
            R.id.action_stationDetails_filter -> findNavController().navigate(R.id.action_stationDetails_to_selectFilterDialog)
            R.id.action_stationDetails_map -> {
                val url = viewModel.getMapURL() ?: return false
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                builder.setShowTitle(false)
                val customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context!!, Uri.parse(url))


            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onServiceClick(position: Int) {
        var serviceStub = viewModel.stationResponse.value?.services?.get(position)?.toServiceStub()
        var activeStation = receivedStation?.toStationStub()!!
        if (serviceStub != null) {
            findNavController().navigate(
                StationDetailsFragmentDirections.actionStationDetailsToServiceDetails(serviceStub, arrayOf(activeStation))
            )


        }
        else
            Log.d(TAG,"Error finding service")
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.title = viewModel.station?.name ?: getString(R.string.fragment_stationDetails_title)
    }

}
