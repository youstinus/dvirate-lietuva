package com.youstinus.dviratelietuva

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.youstinus.dviratelietuva.models.Destination
import com.youstinus.dviratelietuva.models.Route
import com.youstinus.dviratelietuva.ui.info.InfoFragment
import com.youstinus.dviratelietuva.ui.map.MapFragment
import com.youstinus.dviratelietuva.ui.routes.RoutesFragment
import com.youstinus.dviratelietuva.ui.routes.route.RouteFragment
import com.youstinus.dviratelietuva.utilities.PreferencesHelper


class MainActivity : AppCompatActivity(), RoutesFragment.OnRoutesItemFragmentInteractionListener,
    RouteFragment.OnRouteDestinationsItemFragmentInteractionListener {

    val fragmentRoutes: Fragment = RoutesFragment()
    val fragmentMap: Fragment = MapFragment()
    val fragmentInfo: Fragment = InfoFragment()
    val fm: FragmentManager = supportFragmentManager
    var active = fragmentRoutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        /*editNav(navView)
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentInfo, "3").hide(fragmentInfo).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentMap, "2").hide(fragmentMap).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentRoutes, "1").commit()*/

        val navController = findNavController(R.id.nav_host_fragment)

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //supportActionBar?.hide()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_routes, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        navView.setupWithNavController(navController)
        PreferencesHelper.setMapZoomPref(this, mutableListOf(55.2, 23.9, 6.3))
    }

    private fun editNav(navView: BottomNavigationView) {
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_routes -> {
                    fm.beginTransaction().hide(active).show(fragmentRoutes).commit()
                    active = fragmentRoutes
                    true
                }

                R.id.navigation_map -> {
                    fm.beginTransaction().hide(active).show(fragmentMap).commit()
                    active = fragmentMap
                    true
                }

                R.id.navigation_info -> {
                    fm.beginTransaction().hide(active).show(fragmentInfo).commit()
                    active = fragmentInfo
                    true
                }

                else -> {
                    //fm.beginTransaction().hide(active).show(fragmentRoutes).commit()
                    //active = fragmentRoutes
                    false
                }
            }
        }
    }

    override fun onRoutesItemFragmentInteraction(item: Route?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //Snackbar.make(findViewById<ConstraintLayout>(R.id.nav_host_fragment), "Grazuolis", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        val bundle = Bundle()
        bundle.putString("route", Gson().toJson(item))
        findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_route, bundle)
    }

    override fun onRouteDestinationsItemFragmentInteractionListener(
        item: Destination?,
        googleMap: GoogleMap?
    ) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //googleMap.

        if (item != null && googleMap != null) {
            val marker = item.marker
            if (marker != null) {
                marker.showInfoWindow()
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.position), 200, null)
            }
        }
        //println("presses on destination: "+item!!.title)
    }

    override fun onBackPressed() {
// add your code here
        //super.onBackPressed()
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle(getString(R.string.exit))

        // Display a message on alert dialog
        //builder.setMessage(getString(R.string.want_to_exit))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            // Do something when user press the positive button
            //Toast.makeText(applicationContext,"Ok, we change the app background.", Toast.LENGTH_SHORT).show()

            // Change the app background color
            //root_layout.setBackgroundColor(Color.RED)
            //Questions.clearSelections()
            //saveNumberAndDuration()
            //saveQuizProgress()
            //supportActionBar?.show()
            //findViewById<DrawerLayout>(R.id.drawer_layout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            super.onBackPressed()
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
            //Toast.makeText(applicationContext,"You are not agree.", Toast.LENGTH_SHORT).show()
            //unsetSavedProgress()
            //supportActionBar?.show()
            //findViewById<DrawerLayout>(R.id.drawer_layout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            //super.onBackPressed()
        }

        // Display a neutral button on alert dialog
        /*builder.setNeutralButton() { _, _ ->
            //Toast.makeText(applicationContext,"You cancelled the dialog.", Toast.LENGTH_SHORT).show()
        }*/

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
//        super.onBackPressed()
    }
}
