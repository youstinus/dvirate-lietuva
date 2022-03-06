package com.youstinus.dviratelietuva.utilities

class Helper {
    companion object {
        fun getRouteTypeString(routeTypeInt: Int): String {
            var routeType = "bicycle"
            when (routeTypeInt) {
                1 -> routeType = "bicycle"
                2 -> routeType = "vehicle"
            }
            return routeType
        }

        fun getRoardTypeString(roadType: Int): String{
            var roadTypeString = "Mišrus"
            when (roadType) {
                1 -> roadTypeString = "Kelias"
                2 -> roadTypeString = "Takas"
                3 -> roadTypeString = "Miškas"
                4 -> roadTypeString = "Žvirkelis"
                5 -> roadTypeString = "Mišrus"
            }
            return roadTypeString
        }

        fun getDifficultyString(difficulty: Int): String{
            var difficultyString = "Lengvas"
            when (difficulty) {
                1 -> difficultyString = "Lengvas"
                2 -> difficultyString = "Vidutinis"
                3 -> difficultyString = "Sunkus"
                4 -> difficultyString = "Ekstremalus"
            }
            return difficultyString
        }
    }
}