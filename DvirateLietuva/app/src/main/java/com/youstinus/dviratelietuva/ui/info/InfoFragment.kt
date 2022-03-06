package com.youstinus.dviratelietuva.ui.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings.PluginState
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.models.Route


class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment()
    }

    private lateinit var viewModel: InfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        privacyClick(view)
        setOnClickListeners(view)
        //initWebView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(InfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun setOnClickListeners(view: View) {
        //view.findViewById<Button>(R.id.button_upload).setOnClickListener { onUploadClicked() }
        view.findViewById<Button>(R.id.button_create_route).setOnClickListener { onCreateRouteClicked() }
        view.findViewById<Button>(R.id.button_fix_routes).setOnClickListener { onFixRoutesClicked() }
    }

    fun onUploadClicked() {
        findNavController().navigate(R.id.navigation_upload)
    }

    private fun onCreateRouteClicked() {
        findNavController().navigate(R.id.navigation_create_route)
    }

    private fun onFixRoutesClicked() {
        Toast.makeText(context, "", Toast.LENGTH_LONG).show()
        val mf = FirebaseFirestore.getInstance()

        mf.collection("routes").whereEqualTo("state", 3).get().addOnSuccessListener { docs ->

            if (!docs.isEmpty) {
                val documents = docs.toObjects(Route::class.java)
                var i = 0
                for (doc in documents) {

                    var data = mapOf(
                        "state" to 1,
                        "difficulty" to 1,
                        "routeType" to 1,
                        "routeKml" to doc.routeStorage + ".kml",
                        "routeImage" to "",
                        "roadType" to 5
                    )

                    mf.collection("routes").document(docs.documents[i].id).update(data)
                        .addOnFailureListener { ex ->
                            println(ex)
                        }
                    i++
/*
*     //var id: String = "",
    var title: String = "",
    var state: Int = 2, //1-aktyvus, 2-neaktyvus, 3-laukiantis, 4-užblokuotas
    var description: String = "",
    var location: String = "",
    var difficulty: Int = 0, // 1- easy
    var routeType: Int = 1, // 1-bicycle, 2-car
    var routeKml: String = "",
    var routeImage: String = "",
    var routeStorage: String = "",
    var routeUrl: String = "",
    var roadType: Int = 0, // 0- kelias
    var distance: Double = 0.0*/
                }
            }
        }
    }

    fun initWebView(view: View) {
        var webView = view.findViewById<WebView>(R.id.textView_road_type)//webView_map) // todo fix this broken view
        //webView.getSettings().setJavaScriptEnabled(true)
        //webView.loadUrl("https://www.google.com/maps/d/viewer?mid=1QcpiIGO54UdAF1xjKiXR6trKHhiFPLR3");
        val webViewSettings = webView.settings
        webViewSettings.javaScriptCanOpenWindowsAutomatically = true
        webViewSettings.javaScriptEnabled = true
        //webViewSettings.pluginState = PluginState.ON//setPluginsEnabled(true)
        webViewSettings.builtInZoomControls = true
        //webViewSettings.pluginState = PluginState.ON
        val displaymetrics = DisplayMetrics()
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics)
        val density = resources.displayMetrics.density
        val height = displaymetrics.heightPixels / density / 2 - 50
        val width = displaymetrics.widthPixels / density - 20
        webView.loadData(
            "<iframe src=\"https://www.google.com/maps/d/embed?mid=1QcpiIGO54UdAF1xjKiXR6trKHhiFPLR3\" margin=\"0\" padding=\"0\" width=\"" + width + "\" height=\"" + height + "\"></iframe>",
            "text/html",
            "utf-8"
        )
    }

    private fun privacyClick(view:View) {
        val t2 = view.findViewById(R.id.textView_privacy_policy) as TextView
        t2.movementMethod = LinkMovementMethod.getInstance()
        val t3 = view.findViewById(R.id.textView_about) as TextView
        t3.movementMethod = LinkMovementMethod.getInstance()
    }
}
