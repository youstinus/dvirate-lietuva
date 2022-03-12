package com.youstinus.dviratelietuva.utilities

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.youstinus.dviratelietuva.models.Route
import java.io.File


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

            return mFirestore.collection("routes").whereEqualTo("state", 1)
                .orderBy("distance") // active routes
        }

        fun getRoutesAndDraw(view: View, googleMap: GoogleMap) {
            val mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings =
                FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
            mFirestore.collection("routes").whereEqualTo("state", 1)
                .orderBy("distance") //active routes
                .get(Source.DEFAULT)
                .addOnSuccessListener { docs ->
                    //println(docs.metadata.isFromCache)
                    val routes = docs.toObjects(Route::class.java)
                    KmlHelper.drawAllRoutes(view, googleMap, routes)
                    //println(routes)
                }.addOnFailureListener { e ->
                    println(e)
                }
        }

        fun getKML(v: View, route: Route) {
            val routeType = Helper.getRouteTypeString(route.routeType)
            val ref =
                FirebaseStorage.getInstance().reference.child("routes/" + routeType + "/" + route.routeStorage + "/" + route.routeKml)
            val localFile = File(
                Environment.getExternalStorageDirectory(),
                "/" + DIRECTORY_DOWNLOADS + "/" + route.routeKml
            )

            ref.getFile(localFile).addOnSuccessListener { stream ->
                Snackbar.make(
                    v,
                    "Downloaded to " + DIRECTORY_DOWNLOADS + "/" + route.routeKml,
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }.addOnFailureListener { ex ->
                Snackbar.make(v, "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }
        }

        fun downloadFile(
            context: Context,
            fileName: String,
            url: String?
        ) {
            val downloadmanager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri: Uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalFilesDir(
                context,
                DIRECTORY_DOWNLOADS,
                fileName
            )
            downloadmanager.enqueue(request)
        }
    }
}
