package com.anfantanion.traintimes1

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.anfantanion.traintimes1.notify.NotifyManager
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.StationRepo
import com.anfantanion.traintimes1.ui.home.HomeFragment
import com.arlib.floatingsearchview.FloatingSearchView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), HomeFragment.HomeFragmentCallbacks, SharedPreferences.OnSharedPreferenceChangeListener {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    var currentOptionsMenu = R.menu.toolbarmain

    override fun onCreate(savedInstanceState: Bundle?) {
        NotifyManager.setup(applicationContext)
        StationRepo.setActivity(this)
        StationRepo.setContext(applicationContext)
        StationRepo.loadStations()
        JourneyRepo.load(context = applicationContext)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
        onThemeUpdate()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )


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
        JourneyRepo.save(applicationContext)
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            StationRepo.SearchManager.findNearbyLast()
        }else{
            Toast.makeText(this,R.string.requiresLocationInfo,Toast.LENGTH_LONG).show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key){
            "multi_select_list" -> onThemeUpdate()
        }
    }

    fun onThemeUpdate(){
        val x = PreferenceManager.getDefaultSharedPreferences(this).all
        when(x["multi_select_list"]){
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "System" -> {
                if (Build.VERSION.SDK_INT >= 29)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
    }
}
