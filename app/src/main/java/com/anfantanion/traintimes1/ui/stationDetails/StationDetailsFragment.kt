package com.anfantanion.traintimes1.ui.stationDetails

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.models.parcelizable.StationStub
import com.anfantanion.traintimes1.repositories.StationRepo
import kotlinx.android.synthetic.main.fragment_station_details.*
import kotlinx.android.synthetic.main.fragment_station_details.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StationDetails.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StationDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationDetails : Fragment() {
    // TODO: Rename and change types of parameters
    private val TAG = "StationDetails"


    private lateinit var viewModel: StationDetailsViewModel
    private lateinit var stationDetailsRecylerAdapter: StationDetailsRecylerAdapter


    private var receivedStation : Station? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


        stationDetailsRecylerAdapter = StationDetailsRecylerAdapter()



        stationDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        stationDetailsRecyclerView.adapter = stationDetailsRecylerAdapter

        viewModel.stationResponse.observe(viewLifecycleOwner, Observer {
            stationDetailsRecylerAdapter.services = viewModel.stationResponse.value!!.services
            stationDetailsRecylerAdapter.notifyDataSetChanged()
        })

        viewModel.isLoading.observe(viewLifecycleOwner,Observer{
                b -> when(b){
            true -> stationDetailsProgressBar.visibility = View.VISIBLE
            false -> stationDetailsProgressBar.visibility = View.GONE
        }
        })


        viewModel.station = receivedStation
        viewModel.getServices()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StationDetails.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StationDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
