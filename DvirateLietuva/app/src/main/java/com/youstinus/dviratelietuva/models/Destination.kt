package com.youstinus.dviratelietuva.models

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

data class Destination(
    //var id: String = "",
    var title: String = "",
    var latLng: LatLng? = null,
    var marker: Marker? = null
    //var description: String = ""
)