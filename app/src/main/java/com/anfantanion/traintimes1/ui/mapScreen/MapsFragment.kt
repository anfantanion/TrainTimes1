package com.anfantanion.traintimes1.ui.mapScreen

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.stationResponse.ServiceResponse
import com.anfantanion.traintimes1.repositories.StationRepo

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsFragment : Fragment(),
        OnMapReadyCallback{

    val args: MapsFragmentArgs by navArgs()
    var googleMap: GoogleMap? = null;

    lateinit var mapsViewModel : MapsViewModel;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapsViewModel = MapsViewModel(args.displayedServices)

        mapsViewModel.serviceResponses.observe(this){ serviceResponses ->
            drawServices(serviceResponses)
        }



        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    fun drawServices(serviceResponses: Array<ServiceResponse?>) : Boolean{
        var fitService = false
        var fitAll = false
        var currentLocation = false

        when(PreferenceManager.getDefaultSharedPreferences(requireContext()).all["map_focus_list"]){
            "fit-current" -> fitService = true
            "fit-all" -> fitAll = true
            "focus" -> currentLocation = true
        }


        val googleMap1 = this.googleMap ?: return false

        var lowestLat = Double.MAX_VALUE
        var lowestLon = Double.MAX_VALUE
        var highestLat = -Double.MAX_VALUE
        var highestLon = -Double.MAX_VALUE

        for (serviceResponsePos in serviceResponses.indices) {
            val serviceResponse = serviceResponses[serviceResponsePos] ?: continue
            val locations = serviceResponse.locations ?: continue
            var lastPos : LatLng? = null

            if (fitService){
                lowestLat = Double.MAX_VALUE
                lowestLon = Double.MAX_VALUE
                highestLat = -Double.MAX_VALUE
                highestLon = -Double.MAX_VALUE
            }



            for (locPos in locations.indices) {
                val location = locations[locPos]

                val lastKnown = serviceResponse.getMostRecentLocation()
                    ?.let { serviceResponse.getPositionOfStation(it.toStationStub()) }

                val lineColour = if (locPos <= lastKnown ?: 0 )
                    ContextCompat.getColor(this.context!!, R.color.stationPassed)
                else
                    ContextCompat.getColor(this.context!!, R.color.stationNormal)

                val currentPos = StationRepo.getStation(location.toStationStub())!!.latLng()

                if (currentPos.latitude < lowestLat) lowestLat = currentPos.latitude
                if (currentPos.latitude > highestLat) highestLat = currentPos.latitude
                if (currentPos.longitude < lowestLon) lowestLon = currentPos.longitude
                if (currentPos.longitude > highestLon) highestLon = currentPos.longitude

                if (lastPos != null) {
                    googleMap1.addPolyline(
                        PolylineOptions()
                            .add(lastPos, currentPos)
                            .color(lineColour)
                    )
                }

                val stationCircle = CircleOptions()
                    .center(currentPos)
                    .radius(100.0)
                    .fillColor(lineColour)
                    //.strokeWidth(100.0F)
                googleMap1.addCircle(stationCircle)

                lastPos = currentPos
            }


            if ((serviceResponsePos == args.focusedService && fitService ) || (serviceResponsePos == serviceResponses.size-1 && fitAll)){
                if (lowestLat < highestLat && lowestLon < highestLon) {
                    val latLngBounds = LatLngBounds(
                        LatLng(lowestLat, lowestLon),
                        LatLng(highestLat, highestLon)
                    )
                    googleMap1.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 120))
                }
            }

        }

        //val focused = serviceResponses[args.focusedService]
        //val latlng = StationRepo.getStation(focused?.getMostRecentLocation()?.toStationStub())?.latLng()
        //googleMap1.moveCamera(CameraUpdateFactory.newLatLng(latlng))

        return true
    }


    override fun onMapReady(googleMap: GoogleMap) {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        this.googleMap = googleMap

        val sydney = LatLng(51.0, 0.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //drawServices(mapsViewModel.serviceResponses.value!!)
        mapsViewModel.getServiceDetails()
    }


}