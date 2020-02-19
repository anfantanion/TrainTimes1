package com.anfantanion.traintimes1.ui.newJourney


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anfantanion.traintimes1.R

/**
 * A simple [Fragment] subclass.
 */
class NewJourneyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_journey, container, false)
    }


}
