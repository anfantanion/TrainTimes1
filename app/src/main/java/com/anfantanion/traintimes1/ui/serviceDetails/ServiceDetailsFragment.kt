package com.anfantanion.traintimes1.ui.serviceDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import kotlinx.android.synthetic.main.fragment_service_details.*

/**
 * 
 */
class ServiceDetailsFragment : Fragment() {

    private val TAG = "ServiceDetails"
    lateinit var serviceStub : StationStub
    lateinit var serviceDetailsViewModel: ServiceDetailsViewModel
    lateinit var serviceDetailsRecyclerAdapter: ServiceDetailsRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        serviceStub = arguments!!.getParcelable("ActiveService")!!
        Log.d(TAG,serviceStub.name)
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
        serviceDetailsRecyclerAdapter = ServiceDetailsRecyclerAdapter()

        serviceDetailsRecylcerView.layoutManager = LinearLayoutManager(context)
        serviceDetailsRecylcerView.adapter = serviceDetailsRecyclerAdapter



    }

    // TODO: Rename method, update argument and hook method into UI event



}
