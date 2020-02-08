package com.anfantanion.traintimes1.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anfantanion.traintimes1.R
import kotlinx.android.synthetic.main.fragment_search.*


class Search : Fragment() {

    private lateinit var viewModel: SearchViewModel

    private lateinit var searchRecyclerAdapter : SearchRecyclerAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        viewModel.immutableStation.observe(viewLifecycleOwner, Observer { searchRecyclerAdapter.notifyDataSetChanged() })

        searchRecyclerAdapter = SearchRecyclerAdapter()
        searchRecyclerAdapter.stations=viewModel.immutableStation.value ?: emptyList()
        search_recycler.layoutManager = LinearLayoutManager(this.context)
        search_recycler.adapter = searchRecyclerAdapter





        //search_recycler

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
