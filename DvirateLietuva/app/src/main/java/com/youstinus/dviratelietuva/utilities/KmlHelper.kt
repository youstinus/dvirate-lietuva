package com.youstinus.dviratelietuva.utilities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlLineString
import com.google.maps.android.data.kml.KmlPoint
import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.models.Destination
import com.youstinus.dviratelietuva.models.Route
import org.xmlpull.v1.XmlPullParserException
import java.io.*


class KmlHelper {
    companion object {
        var HUES: MutableList<Float> =
            (MutableList(45) { x -> (8 * x).toFloat() }).shuffled().toMutableList()
        var COLORS: MutableList<Int> = MutableList(45) { x ->
            Color.HSVToColor(
                mutableListOf<Float>(
                    HUES[x],
                    100.0f,
                    100.0f
                ).toFloatArray()
            )
        }

        /*mutableListOf(
        Color.RED.and(Color.BLACK),
        Color.rgb(255, 165, 0),//orange
        Color.YELLOW,
        Color.GREEN,
        Color.CYAN,
        Color.rgb(0, 127, 255),//azure
        Color.BLUE,
        Color.rgb(128, 0, 255),// violet
        Color.MAGENTA,
        Color.rgb(255, 192, 203) //rose
    )*/


/*public static final float HUE_RED = 0.0F;
    public static final float HUE_ORANGE = 30.0F;
    public static final float HUE_YELLOW = 60.0F;
    public static final float HUE_GREEN = 120.0F;
    public static final float HUE_CYAN = 180.0F;
    public static final float HUE_AZURE = 210.0F;
    public static final float HUE_BLUE = 240.0F;
    public static final float HUE_VIOLET = 270.0F;
    public static final float HUE_MAGENTA = 300.0F;
    public static final float HUE_ROSE = 330.0F;*/
        //var colorIndex: Int = 0

        fun setKmlLayer(context: Context, googleMap: GoogleMap, route: Route) {
            val mGoogleMap = googleMap
            mGoogleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(55.2, 24.0),
                    6f
                )
            )

            val builder = LatLngBounds.Builder()

            val routeType = Helper.getRouteTypeString(route.routeType)

            val islandRef = FirebaseStorage.getInstance()
                .reference.child("routes/" + routeType + "/" + route.routeStorage + "/" + route.routeKml)

            val ONE_MEGABYTE: Long = 1024 * 1024
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { stream ->
                // Data for "images/island.jpg" is returned, use this as needed
                val myInputStream = ByteArrayInputStream(stream)//stream.stream//


                // change next line for your kml source
                val kmlInputStream = myInputStream//context.resources.openRawResource(R.raw.map)


                try {
                    val kmlLayer = KmlLayer(mGoogleMap, kmlInputStream, context)
                    kmlLayer.addLayerToMap()

                    // todo implement recursive search for coordinates
                    // todo implement custom lineString points getter
                    val pathPoints = mutableListOf<LatLng>()
                    if (kmlLayer.containers != null) {
                        //for (container2 in kmlLayer.getContainers()) {
                        //    if (container2.hasContainers()) {
                        for (container in kmlLayer.containers) {
                            if (container.hasPlacemarks()) {
                                for (placemark in container.placemarks) {
                                    val geometry = placemark.geometry
                                    if (geometry.geometryType == "Point") {
                                        val point = placemark.geometry as KmlPoint
                                        val latLng = LatLng(
                                            point.geometryObject.latitude,
                                            point.geometryObject.longitude
                                        )
                                        pathPoints.add(latLng)
                                    } else if (geometry.geometryType == "LineString") {
                                        val kmlLineString = geometry as KmlLineString
                                        val coords = kmlLineString.geometryObject
                                        for (latLng in coords) {
                                            pathPoints.add(latLng)
                                        }
                                    }
                                }
                            }
                            //    }
                            // }
                        }

                        for (latLng in pathPoints) {
                            //mGoogleMap.addMarker(MarkerOptions().position(latLng))
                            builder.include(latLng)
                        }
                        if (pathPoints.size > 0) {
                            val bounds = builder.build()
                            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                            googleMap.animateCamera(cu, 500, null)
                        }
                    }

                } catch (e: XmlPullParserException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.addOnFailureListener {
                // Handle any errors
            }
            //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(LatLng(54.0, 23.0), LatLng(55.0, 24.0)), 0))
        }

        fun drawCustomPoints(
            context: Context,
            googleMap: GoogleMap,
            route: Route,
            callBack: (names: MutableList<Destination>) -> Unit
        ) {
            val mGoogleMap = googleMap
            mGoogleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(55.2, 24.0),
                    6f
                )
            )

            val builder = LatLngBounds.Builder()

            val routeType = Helper.getRouteTypeString(route.routeType)

            val islandRef = FirebaseStorage.getInstance()
                .reference.child("routes/" + routeType + "/" + route.routeStorage + "/" + route.routeKml)

            val ONE_MEGABYTE: Long = 1024 * 1024
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { stream ->
                // Data for "images/island.jpg" is returned, use this as needed
                val myInputStream = ByteArrayInputStream(stream)//stream.stream//
                myInputStream.mark(0)
                // change next line for your kml source
                val kmlInputStream = myInputStream//context.resources.openRawResource(R.raw.map)

                try {
                    val kmlLayer = KmlLayer(mGoogleMap, kmlInputStream, context)
                    kmlLayer.addLayerToMap()

                    // todo implement recursive search for coordinates
                    // todo implement custom lineString points getter
                    val names = mutableListOf<Destination>()
                    val pathPoints = mutableListOf<LatLng>()
                    if (kmlLayer.containers != null) {
                        //for (container2 in kmlLayer.getContainers()) {
                        //    if (container2.hasContainers()) {
                        for (container in kmlLayer.containers) {
                            if (container.hasPlacemarks()) {
                                for (placemark in container.placemarks) {
                                    val geometry = placemark.geometry
                                    if (geometry.geometryType == "Point") {
                                        try {
                                            val it =
                                                (placemark.properties.iterator())// as HashMap<String, String>)["name"]
                                            while (it.hasNext()) {
                                                val entry =
                                                    it.next() as Map.Entry<*, *> //current entry in a loop
                                                val name = entry.value as String
                                                if (name != "") {
                                                    val point = placemark.geometry as KmlPoint
                                                    val latlng = LatLng(
                                                        point.geometryObject.latitude,
                                                        point.geometryObject.longitude
                                                    )
                                                    names.add(Destination(name, latlng))
                                                }
                                            }

                                        } catch (ex: Exception) {
                                            println(ex)
                                        }
                                    }
                                }
                            }
                            //    }
                            // }
                        }

                        /*googleMap.addPolyline(
                            PolylineOptions()
                                .clickable(false)
                                .addAll(
                                    pathPoints
                                ).width(10f).color(Color.BLUE).geodesic(true))*/

                    }

                    //val markers = mutableListOf<Marker>()

                    for (poi in names) {
                        val lat = poi.latLng
                        if (lat != null) {
                            poi.marker =
                                mGoogleMap.addMarker(MarkerOptions().position(lat).title(poi.title))
                        }
                    }

                    myInputStream.reset()
                    val pointsPoints = getCustomPoints(inputStreamToString(myInputStream))

                    for (points in pointsPoints) {

                        for (latLng in points) {
                            builder.include(latLng)
                        }
                    }
                    if (pointsPoints.size > 0) {
                        val bounds = builder.build()
                        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        googleMap.animateCamera(cu, 500, null)
                    }

                    callBack.invoke(names)
                    //kmlLayer.removeLayerFromMap()

                } catch (e: XmlPullParserException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.addOnFailureListener {
                // Handle any errors
            }
        }

        fun drawAllRoutes(view: View, googleMap: GoogleMap, routes: MutableList<Route>) {
            routes.forEachIndexed { index, route ->
                val routeType = Helper.getRouteTypeString(route.routeType)
                val islandRef = FirebaseStorage.getInstance()
                    .reference.child("routes/" + routeType + "/" + route.routeStorage + "/" + route.routeKml)
                val FIVE_MEGABYTES: Long = 5 * 1024 * 1024
                islandRef.getBytes(FIVE_MEGABYTES).addOnSuccessListener { stream ->
                    // Data for "images/island.jpg" is returned, use this as needed
                    val myInputStream = ByteArrayInputStream(stream)//stream.stream//

                    // change next line for your kml source
                    //val kmlInputStream = myInputStream//context.resources.openRawResource(R.raw.map)

                    try {
                        /*val kmlLayer = KmlLayer(googleMap, kmlInputStream, view.context)
                        kmlLayer.addLayerToMap()

                        // todo implement recursive search for coordinates
                        // todo implement custom lineString points getter
                        val pathPoints = mutableListOf<LatLng>()
                        if (kmlLayer != null && kmlLayer.getContainers() != null) {
                            //for (container2 in kmlLayer.getContainers()) {
                            //    if (container2.hasContainers()) {
                            for (container in kmlLayer.getContainers()) {
                                if (container.hasPlacemarks()) {
                                    for (placemark in container.getPlacemarks()) {
                                        val geometry = placemark.getGeometry()
                                        if (geometry.getGeometryType() == "Point") {
                                            val point = placemark.getGeometry() as KmlPoint
                                            val latLng = LatLng(
                                                point.geometryObject.latitude,
                                                point.geometryObject.longitude
                                            )
                                            pathPoints.add(latLng)
                                        } else if (geometry.getGeometryType() == "LineString") {
                                            val kmlLineString = geometry as KmlLineString
                                            val coords = kmlLineString.geometryObject
                                            for (latLng in coords) {
                                                pathPoints.add(latLng)
                                            }
                                        }
                                    }
                                }
                                //    }
                                // }
                            }

                            /*googleMap.addPolyline(
                                PolylineOptions()
                                    .clickable(true)
                                    .addAll(
                                        pathPoints
                                    ).width(10f).color(Color.BLUE).geodesic(true))*/



                        }

                        kmlLayer.removeLayerFromMap()*/

                        val pointsPoints = getCustomPoints(inputStreamToString(myInputStream))

                        for (points in pointsPoints) {
                            googleMap.addPolyline(
                                PolylineOptions()
                                    .clickable(true)
                                    .addAll(
                                        points
                                    ).width(5f).color(COLORS[index % COLORS.size])
                            )
                        }

                        val max = pointsPoints.maxByOrNull { x -> x.size }
                        if (max != null && max.size > 0) {
                            googleMap.addMarker(
                                MarkerOptions().position(max[0]).title(route.title).draggable(
                                    false
                                ).icon(BitmapDescriptorFactory.defaultMarker(HUES[index % HUES.size]))
                            )?.tag = route
                        }

                    } catch (e: XmlPullParserException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.addOnFailureListener {
                    // Handle any errors
                }

            }

            googleMap.setOnMarkerClickListener { marker ->
                onMarkerClicked(
                    view,
                    marker
                )
            }


        }

        private fun onMarkerClicked(view: View, marker: Marker): Boolean {
            val tag: Route = marker.tag as Route

            val bundle = Bundle()
            bundle.putString("route", Gson().toJson(tag))
            view.findNavController().navigate(R.id.navigation_route, bundle)

            return true
        }

        private fun inputStreamToString(inputStream: InputStream): String {
            val r = BufferedReader(InputStreamReader(inputStream))
            val total = StringBuilder()
            var line = r.readLine()
            while (line != null) {
                total.append(line).append('\n')
                line = r.readLine()
            }
            return total.toString()
        }

        private fun getCustomPoints(total: String): MutableList<MutableList<LatLng>> {
            val pointsPoints = mutableListOf<MutableList<LatLng>>()

            val regex =
                Regex("(?<=(<coordinates>))(\\w|\\d|\\n|[().,\\-:;@#\$%^&*\\[\\]\"'+–/\\/®°⁰!?{}|`~]| )+?(?=(</coordinates>))")
            val matches = regex.findAll(total)

            for (match in matches) {
                val points = getPointsFromString(match.value)
                if (points.size > 0) {
                    pointsPoints.add(points)
                }
            }

            return pointsPoints
        }

        private fun getPointsFromString(coor: String): MutableList<LatLng> {
            val points = mutableListOf<LatLng>()

            val matches = Regex("\\s+").split(coor)
            for (match in matches) {
                val coordinates = Regex(",").split(match)
                try {
                    points.add(LatLng(coordinates[1].toDouble(), coordinates[0].toDouble()))
                } catch (ex: Exception) {

                }
            }

            return points
        }
    }
}