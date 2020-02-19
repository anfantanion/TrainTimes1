package com.anfantanion.traintimes1.ui.serviceDetails

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.parcelizable.ServiceStub
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import kotlinx.android.synthetic.main.fragment_service_details.*

/**
 * 
 */
class ServiceDetailsFragment : Fragment(), ServiceDetailsRecyclerAdapter.ServiceDetailsRecycClick {

    private val TAG = "ServiceDetails"
    lateinit var serviceStub : ServiceStub
    lateinit var serviceDetailsViewModel: ServiceDetailsViewModel
    lateinit var serviceDetailsRecyclerAdapter: ServiceDetailsRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        serviceStub = arguments!!.getParcelable("ActiveService")!!
        Log.d(TAG,serviceStub.serviceUid)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceDetailsViewModel = ViewModelProvider(this).get(ServiceDetailsViewModel::class.java)
        serviceDetailsViewModel.service = serviceStub

        serviceDetailsRecyclerAdapter = ServiceDetailsRecyclerAdapter(this)

        serviceDetailsRecylcerView.layoutManager = LinearLayoutManager(context)
        serviceDetailsRecylcerView.adapter = serviceDetailsRecyclerAdapter

        serviceDetailsViewModel.serviceResponse.observe(viewLifecycleOwner, Observer{
            serviceDetailsRecyclerAdapter.locations=it.locations
            serviceDetailsRecyclerAdapter.notifyDataSetChanged()
            setTitle()
        })

        serviceDetailsViewModel.isLoading.observe(viewLifecycleOwner,Observer{
                b -> when(b){
            true -> serviceDetailsProgressBar.visibility = View.VISIBLE
            false -> serviceDetailsProgressBar.visibility = View.GONE
        }
        })

        serviceDetailsViewModel.isError.observe(viewLifecycleOwner,Observer{
                b -> when(b){
            true -> Toast.makeText(context,"Error "+serviceDetailsViewModel.lastError.toString(), Toast.LENGTH_LONG).show()
        }
        })



        serviceDetailsViewModel.getServiceDetails()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_servicedetails, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setTitle() {
        (activity as MainActivity).supportActionBar?.title = serviceDetailsViewModel.serviceResponse.value?.getName() ?: getString(R.string.fragment_serviceDetails_title)
    }

    override fun onStationClick(position: Int) {
        val x = serviceDetailsViewModel.serviceResponse.value?.locations?.get(position)
        if (x != null) {
            val stationStub = StationStub(x.crs)
            findNavController().navigate(
                R.id.action_serviceDetails_to_stationDetails,
                bundleOf("ActiveStation" to stationStub)
            )
        }
        else
            Log.d(TAG,"Error finding service")
    }


}
