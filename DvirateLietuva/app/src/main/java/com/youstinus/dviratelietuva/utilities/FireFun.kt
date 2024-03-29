package com.youstinus.dviratelietuva.utilities

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.youstinus.dviratelietuva.models.Route
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


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
                //.orderBy("distance") //active routes
                .get(Source.DEFAULT)
                .addOnSuccessListener { docs ->
                    //println(docs.metadata.isFromCache)
                    val routes = docs.toObjects(Route::class.java)

                    // todo CHOOSE YOUR PROVIDER FOR MAP ROUTES
                    // KmlHelper.drawAllRoutes(view, googleMap, routes)
                    KmlHelper.drawAllRoutesWithPaths(view, googleMap, routes)

                    //println(routes)
                }.addOnFailureListener { e ->
                    println(e)
                }
        }

        fun getKML(activity: Activity, v: View, route: Route) {
            val REQUEST_EXTERNAL_STORAGE = 1
            val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            val permission = activity.checkSelfPermission(
                //activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                activity.requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
                /*ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )*/
            }

            val routeType = Helper.getRouteTypeString(route.routeType)
            val ref =
                FirebaseStorage.getInstance().reference.child("routes/" + routeType + "/" + route.routeStorage + "/" + route.routeKml)
            // var dir = activity.getExternalFilesDir(null) // Environment.getExternalStorageDirectory()
            val dir = Environment.getExternalStorageDirectory()
            val name = removeSuffix(route.routeKml, ".kml")
            var localFile = File(dir, "$DIRECTORY_DOWNLOADS/$name.kml")

            var num = 0
            var save: String
            while (localFile.exists()) {
                if (localFile.delete()) { // sometimes deleted file still does not let create new one
                    break
                }

                save = name + num++
                localFile = File(dir, "$DIRECTORY_DOWNLOADS/$save.kml")
            }

            ref.getFile(localFile).addOnSuccessListener { stream ->
                Snackbar.make(
                    v,
                    "Downloaded",// to " + DIRECTORY_DOWNLOADS + "/" + route.routeKml,
                    500, // Snackbar.LENGTH_SHORT
                ).setAction("Action", null).show()
            }.addOnFailureListener { ex1 ->
                val simpleDate = SimpleDateFormat("mmss")
                val currentDate = simpleDate.format(Date())
                val file = File(dir, "$DIRECTORY_DOWNLOADS/$name$currentDate.kml")
                ref.getFile(file)
                    .addOnSuccessListener { stream ->
                        Snackbar.make(
                            v,
                            "Downloaded",// to " + DIRECTORY_DOWNLOADS + "/" + route.routeKml,
                            500, // Snackbar.LENGTH_SHORT
                        ).setAction("Action", null).show()
                    }.addOnFailureListener { ex ->
                        Snackbar.make(v, "Failed", 500 /*Snackbar.LENGTH_SHORT*/)
                            .setAction("Action", null)
                            .show()
                    }
            }

//            ref.getBytes(5000000).addOnSuccessListener { bytes ->
//                val resolver = activity.contentResolver
//                val uri = localFile.toUri()
//                resolver.openFileDescriptor(uri, "w").use { parcelFileDescriptor ->
//                    ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor)
//                        .write(bytes)
//                }
//            }.addOnFailureListener { ex ->
//                Snackbar.make(v, "Failed", Snackbar.LENGTH_LONG).setAction("Action", null).show()
//            }
        }

        fun removeSuffix(s: String?, suffix: String?): String? {
            return if (s != null && suffix != null && s.endsWith(suffix)) {
                s.substring(0, s.length - suffix.length)
            } else s
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

        fun hasPermissions(context: Context?, permissions: List<String>): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
                return permissions.all { permission ->
                    ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }

            return true
        }
    }
}
