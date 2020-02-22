package com.anfantanion.traintimes1

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anfantanion.traintimes1.repositories.StationRepo
import com.anfantanion.traintimes1.ui.home.HomeFragment
import com.arlib.floatingsearchview.FloatingSearchView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), HomeFragment.HomeFragmentCallbacks {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    var currentOptionsMenu = R.menu.toolbarmain

    override fun onCreate(savedInstanceState: Bundle?) {
        StationRepo.setContext(applicationContext)
        StationRepo.loadStations()



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        var x = Journey.JourneyPlanner()
//        x.plan(listOf(StationStub("AXM"),StationStub("CLJ"),StationStub("RDH")))
//        RTTAPI.requestStation(
//            "AXM",
//            listener = Response.Listener { response ->
//                val x = response },
//            errorListener = Response.ErrorListener { error ->
//                val x = error; },
//            maxAge = 60000
//
//        );
//
//        RTTAPI.requestStation(
//            "AXM",
//            listener = Response.Listener { response ->
//                val x = response },
//            errorListener = Response.ErrorListener { error ->
//                val x = error; },
//            maxAge = 60000
//
//        );


//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_active_journey, R.id.nav_saved_journeys,
                R.id.nav_settings, R.id.nav_menu_about, R.id.nav_frag_search
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.toolbarmain, menu)
        //return true
        //menuInflater.inflate(currentOptionsMenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        when (item.itemId) {
            R.id.action_settings -> navController.navigate(R.id.nav_settings)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onAttachSearchViewToDrawer(searchView: FloatingSearchView) {
        searchView.attachNavigationDrawerToMenuButton(drawerLayout)
    }

    override fun hideActionBar() {
        actionBar?.hide()
    }

    override fun showActionBar() {
        actionBar?.show()
    }

    override fun onPause() {
        StationRepo.SearchManager.save()
        super.onPause()
    }
}
