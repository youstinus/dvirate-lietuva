package com.youstinus.dviratelietuva.utilities

fun Int.cuToRoadType(): String {
    when (this) {
        0 -> return "Kelias"
        1 -> return "Takas"
        2 -> return "Miškas"
        3 -> return "Žvirkelis"
        4 -> return "Mišrus"
        else -> return "Mišrus"
    }
}

fun Int.cuToDifficultyType(): String {
    when (this) {
        0 -> return "Lengvas"
        1 -> return "Vidutinis"
        2 -> return "Sunkus"
        3 -> return "Ekstremalus"
        else -> return ""
    }
}