package com.youstinus.dviratelietuva.utilities

import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.youstinus.dviratelietuva.models.Route

class FireFun {
    companion object {
        fun getRoutes(): Query {
            val mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

            /*mFirestore.collection("routes")
                .get()
                .addOnSuccessListener{docs->
                    println(docs)
                    var routes = docs.toObjects(Route::class.java)
                    println(routes)
                }.addOnFailureListener {e->
                    println(e)
                }*/

            val query: Query = mFirestore.collection("routes").whereEqualTo("state", 1).orderBy("distance") // active routes
            return query
        }

        fun getRoutesAndDraw(view: View, googleMap: GoogleMap) {
            val mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

            mFirestore.collection("routes").whereEqualTo("state", 1).orderBy("distance") //active routes
                .get()
                .addOnSuccessListener{docs->
                    println(docs)
                    val routes = docs.toObjects(Route::class.java)
                    //routes.sortBy { x->x.distance }
                    KmlHelper.drawAllRoutes(view, googleMap, routes)
                    println(routes)
                }.addOnFailureListener {e->
                    println(e)
                }
        }
    }
}
