package com.youstinus.dviratelietuva.utilities

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    fun getEmailPref(context: Context?): String {
        val mPrefs: SharedPreferences? =
            context?.getSharedPreferences("dviratelietuva", Context.MODE_PRIVATE)
        return mPrefs?.getString("email", "") ?: ""
    }

    fun setEmailPref(context: Context?, email: String) {
        val mPrefs: SharedPreferences? =
            context?.getSharedPreferences("dviratelietuva", Context.MODE_PRIVATE)
        val prefsEditor = mPrefs?.edit()
        prefsEditor?.putString("email", email)
        prefsEditor?.apply()
    }

    fun getMapZoomPref(context: Context?): MutableList<Double> {
        val mPrefs: SharedPreferences? =
            context?.getSharedPreferences("dviratelietuva", Context.MODE_PRIVATE)
        val str = mPrefs?.getString("mapzoom", "") ?: ""
        val mapZooms = Regex("\\|").split(str)
        if (mapZooms.size == 3) {
            return mutableListOf(mapZooms[0].toDouble(), mapZooms[1].toDouble(), mapZooms[2].toDouble())
        }
        return mutableListOf(55.2, 23.9, 6.3)
    }

    fun setMapZoomPref(context: Context?, mapZooms: MutableList<Double>) {
        val mPrefs: SharedPreferences? =
            context?.getSharedPreferences("dviratelietuva", Context.MODE_PRIVATE)
        val prefsEditor = mPrefs?.edit()
        val mapZoom = mapZooms.joinToString("|")
        prefsEditor?.putString("mapzoom", mapZoom)
        prefsEditor?.apply()
    }
}