package com.anfantanion.traintimes1.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.Station
import com.anfantanion.traintimes1.repositories.StationRepo
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.arlib.floatingsearchview.util.Util
import kotlinx.android.synthetic.main.dialog_search.view.*

class SearchDialog(
    val searchDialogListener: (stationSuggestion: Station.StationSuggestion) -> Unit
) : DialogFragment(){

    lateinit var searchView : FloatingSearchView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_search, null)
        builder.setView(view)

        searchView = view.dialog_search_search1

        searchView.setOnQueryChangeListener{ oldQuery: String, newQuery: String ->
            if (oldQuery != "" && newQuery == "") {
                searchView.clearSuggestions()
            }
            else {
                StationRepo.SearchManager.findSuggestions(newQuery,5, object : StationRepo.SearchManager.stationSuggestionListener {
                    override fun onResults(results: List<Station.StationSuggestion>) {
                        searchView.swapSuggestions(results)
                    }
                })
            }
        }

        searchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                searchDialogListener(searchSuggestion as Station.StationSuggestion)
                destroy()
            }
            override fun onSearchAction(query: String?) {
                StationRepo.SearchManager.lastSearchTopStationSuggestion?.let{searchDialogListener(it)}
                destroy()
            }
        })

        searchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() { //show suggestions when search bar gains focus (typically history suggestions)
                searchView.swapSuggestions(StationRepo.SearchManager.getHistory(3))
            }

            override fun onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                //searchView.setSearchBarTitle("")
                dialog!!.dismiss()


            }
        })

        searchView.setOnHomeActionClickListener {
            destroy()
        }

        val finalDialog = builder.create()
        finalDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        finalDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)

        finalDialog.setOnShowListener {
            searchView.setSearchFocused(true)
        }
        return finalDialog
    }

    private fun destroy(){
        searchView.setSearchFocused(false)
        searchView.clearSearchFocus()


    }
}