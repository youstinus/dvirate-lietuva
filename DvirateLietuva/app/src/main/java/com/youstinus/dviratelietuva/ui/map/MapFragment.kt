package com.youstinus.dviratelietuva.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.utilities.FireFun
import com.youstinus.dviratelietuva.utilities.MAPVIEW_BUNDLE_KEY
import com.youstinus.dviratelietuva.utilities.PreferencesHelper

class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var viewModel: MapViewModel
    private lateinit var mMapView: MapView
    private var gMap: GoogleMap? = null
    private var drawed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        initializeMap(view, savedInstanceState)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (context == null || view == null) {
            return
        }

        gMap = googleMap

        val mapZoom = PreferencesHelper.getMapZoomPref(context)

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(mapZoom[0], mapZoom[1]),
                mapZoom[2].toFloat()
            )
        )
        //googleMap.setMyLocationEnabled(true);

        FireFun.getRoutesAndDraw(requireView(), googleMap)

        //KmlHelper.setKmlLayer(context!!, googleMap, route!!)

        /*googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(

                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.364, 147.891),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)
                )
        )*/
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
        //googleMap.setOnPolylineClickListener(this)
        //googleMap.setOnPolygonClickListener(this)
        // Add a latLng in Sydney and move the camera
        /*val kaunas = LatLng(53.0, 23.0)
        googleMap.addMarker(MarkerOptions().position(kaunas).title("Marker in Kaunas"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(kaunas))*/
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart();
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume();
    }

    override fun onPause() {
        mMapView.onPause();
        val map = gMap
        if (map != null) {
            val dist = map.cameraPosition.zoom
            val lat = map.cameraPosition.target.latitude
            val lng = map.cameraPosition.target.longitude
            PreferencesHelper.setMapZoomPref(
                context,
                mutableListOf(lat.toDouble(), lng.toDouble(), dist.toDouble())
            )
        }
        //onSaveInstanceState(Bundle())
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop();
    }

    override fun onDestroy() {
        mMapView.onDestroy();
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory();
    }

    private fun initializeMap(view: View, savedInstanceState: Bundle?) {
        //mapView_map
        mMapView = view.findViewById(R.id.mapView_map)
        var mapViewBundle = Bundle()
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)!!
        }
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }
}
