package com.anfantanion.traintimes1.ui.activeJourney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anfantanion.traintimes1.R

class ActiveJourneyFragment : Fragment() {

    private lateinit var activeJourneyViewModel: ActiveJourneyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activeJourneyViewModel =
            ViewModelProviders.of(this).get(ActiveJourneyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_active_journey, container, false)
        val textView: TextView = root.findViewById(R.id.active_Journey)
        activeJourneyViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}