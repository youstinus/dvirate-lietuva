package com.youstinus.dviratelietuva.ui.routes.route

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.maps.*

import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.models.Route
import com.youstinus.dviratelietuva.utilities.MAPVIEW_BUNDLE_KEY
import com.youstinus.dviratelietuva.utilities.KmlHelper
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.youstinus.dviratelietuva.models.Destination
import com.youstinus.dviratelietuva.ui.routes.MyRoutesRecyclerViewAdapter
import com.youstinus.dviratelietuva.ui.routes.RoutesFragment
import com.youstinus.dviratelietuva.utilities.FireFun
import com.youstinus.dviratelietuva.utilities.Helper

const val ARG_ROUTE = "route"

//fun Double.format(digits: Int) = "%.${digits}f".format(this)

class RouteFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = RouteFragment()
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RoutesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private var columnCount = 1
    private var listener: OnRouteDestinationsItemFragmentInteractionListener? = null

    private var paramRoute: String? = null
    private var route: Route? = null
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mMapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            paramRoute = it.getString(ARG_ROUTE)
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        if (paramRoute != null) {
            route = Gson().fromJson<Route>(paramRoute, Route::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_route, container, false)

        mMapView = view.findViewById(R.id.mapView)
        var mapViewBundle = Bundle()
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)!!
        }
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)

        //view.findViewById<WebView>(R.id.webView).loadData("<iframe src=\"https://www.google.com/maps/d/embed?mid=12jBeyKQh4uZRI4qqDdLigSX-my05bEGn\" width=\"640\" height=\"480\"></iframe>", "text/html", "utf-8")
        //view.findViewById<WebView>(R.id.webView).loadUrl("https://www.google.com/maps/d/embed?mid=12jBeyKQh4uZRI4qqDdLigSX-my05bEGn")

        //setDestinationsRecyclerView(view)

        populateRouteInfo(view)
        setOnClickListeners(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRouteDestinationsItemFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnRoutesItemFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        //findNavController().navigate(R.id.action_navigation_route_to_navigation_routes)
        //findNavController().popBackStack()
        super.onDetach()
        listener = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (context == null || route == null || view == null) {
            return
        }

        mGoogleMap = googleMap
        //KmlHelper.setKmlLayer(context!!, googleMap, route!!)
        KmlHelper.drawCustomPoints(requireContext(), googleMap, route!!) { names ->
            if (view != null) {
                if (names.size > 0) {
                    setDestinationsRecyclerView(requireView(), names)
                } else {
                    setDestinationsRecyclerView(requireView(), mutableListOf(Destination("Nėra objektų")))
                }
            }
        }

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
        mMapView.onStart()
        super.onStart()
    }

    override fun onResume() {
        mMapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mMapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        //findNavController().popBackStack()
        //findNavController().navigateUp()
        //
        mMapView.onDestroy();
        super.onDestroy()
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

    override fun onLowMemory() {
        mMapView.onLowMemory()
        super.onLowMemory()
    }

    fun populateRouteInfo(view: View) {
        if (route == null)
            return

        val r = route!!
        view.findViewById<TextView>(R.id.textView_title).text = r.title
        view.findViewById<TextView>(R.id.textView_location).text = r.location
        view.findViewById<TextView>(R.id.textView_description).text = r.description
        view.findViewById<TextView>(R.id.textView_distance).text = "${r.distance.format(1)}km"
        view.findViewById<TextView>(R.id.textView_road_type).text =
            Helper.getRoardTypeString(r.roadType)
        view.findViewById<TextView>(R.id.textView_difficulty).text =
            Helper.getDifficultyString(r.roadType)

        if (r.routeImage != "") {
            val routeImage = view.findViewById<ImageView>(R.id.imageView_route)
            loadImage(routeImage, r)
        }
    }

    fun setOnClickListeners(view: View) {
        view.findViewById<Button>(R.id.button_open).setOnClickListener { onOpenClick() }
        view.findViewById<Button>(R.id.button_kml).setOnClickListener { onKMLDownload()        }
    }

    private fun onOpenClick() {
        if (route!!.routeUrl == "")
            return

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(route!!.routeUrl))
        //https://drive.google.com/open?id=12jBeyKQh4uZRI4qqDdLigSX-my05bEGn // https://maps.google.com/maps/d/edit?mid=12jBeyKQh4uZRI4qqDdLigSX-my05bEGn // https://goo.gl/maps/ZtbPGScQzeqAgh4X8 //"https://maps.google.com/maps/d/viewer?mid=12jBeyKQh4uZRI4qqDdLigSX-my05bEGn"
        //browserIntent.setPackage("com.google.android.apps.maps")
        startActivity(browserIntent)
    }

    private fun onKMLDownload() {
        if (route!!.routeKml == "")
            return

        FireFun.getKML(requireView(), route!!)
    }

    fun setDestinationsRecyclerView(view: View, names: MutableList<Destination>) {
        // Set the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_destinations)
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyRouteDestinationsRecyclerViewAdapter(
                    mGoogleMap,
                    names,
                    listener
                )
            }
            recyclerView.adapter?.notifyDataSetChanged();
        }
    }

    private fun loadImage(imageView: ImageView, r: Route) {
        val routeType = Helper.getRouteTypeString(r.routeType)
        val ref = FirebaseStorage.getInstance()
            .reference.child("routes/" + routeType + "/" + r.routeStorage + "/" + r.routeImage)
        ref.downloadUrl.addOnSuccessListener { uri ->
            if (uri != null && uri.toString() != "") {
                Picasso.get().load(uri).into(imageView)
                imageView.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
        }
    }

    interface OnRouteDestinationsItemFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onRouteDestinationsItemFragmentInteractionListener(item: Destination?, googleMap: GoogleMap?)
    }
}
