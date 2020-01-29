package com.anfantanion.traintimes1.ui.savedJourneys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anfantanion.traintimes1.R

class SavedJourneysFragment : Fragment() {

    private lateinit var savedJourneysViewModel: SavedJourneysViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        savedJourneysViewModel =
            ViewModelProviders.of(this).get(SavedJourneysViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_saved_journeys, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        savedJourneysViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}