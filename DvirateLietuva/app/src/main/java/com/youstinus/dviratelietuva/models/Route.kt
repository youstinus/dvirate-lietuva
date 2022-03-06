package com.youstinus.dviratelietuva.models

data class Route(
    //var id: String = "",
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
    var distance: Double = 0.0
)