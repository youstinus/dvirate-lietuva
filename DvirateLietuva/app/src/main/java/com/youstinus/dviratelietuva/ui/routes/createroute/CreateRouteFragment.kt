package com.youstinus.dviratelietuva.ui.routes.createroute

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.ui.info.InfoViewModel

class CreateRouteFragment : Fragment() {

    companion object {
        fun newInstance() = CreateRouteFragment()
    }

    private lateinit var viewModel: CreateRouteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_route, container, false)


        setOnClickListener(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[CreateRouteViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClickListener(view: View) {
        view.findViewById<Button>(R.id.button).setOnClickListener { onSubmitClick() }
    }

    private fun onSubmitClick() {
        if (view == null) {
            return
        }

        val data = hashMapOf(
            "title" to requireView().findViewById<EditText>(R.id.editText_title).text.toString(),
            "description" to requireView().findViewById<EditText>(R.id.editText_description).text.toString(),
            "distance" to requireView().findViewById<EditText>(R.id.editText_distance).text.toString()
                .toFloat(),
            "location" to requireView().findViewById<EditText>(R.id.editText_location).text.toString(),
            "routeStorage" to requireView().findViewById<EditText>(R.id.editText_route_storage).text.toString(),
            "routeUrl" to requireView().findViewById<EditText>(R.id.editText_route_url).text.toString(),
            "state" to 1,
            "difficulty" to 1,
            "routeType" to 1,
            "routeKml" to requireView().findViewById<EditText>(R.id.editText_route_storage).text.toString() + ".kml",
            "routeImage" to "",
            "roadType" to 5
        )

        FirebaseFirestore.getInstance().collection("routes").add(data).addOnSuccessListener { _ -> // doc ->
            Toast.makeText(requireView().context, "Good", Toast.LENGTH_LONG).show()

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
        }.addOnFailureListener {
            //val ex = it
            Toast.makeText(requireView().context, "Permission", Toast.LENGTH_LONG).show()
//            System.out.println(ex.message)
        }

    }
}
