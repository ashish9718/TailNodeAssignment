package com.ashish.tailnodeassignment.utils

import android.content.Context
import android.content.SharedPreferences

class PrefHelper(var context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("file", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()

    var isFirstTimeLaunch: Boolean
        get() = preferences.getBoolean("IS_FIRST_LAUNCH", true)
        set(value) {
            editor.putBoolean("IS_FIRST_LAUNCH", value)
            editor.commit()
        }

    var name: String
        get() = preferences.getString("NAME", "").toString()
        set(value) {
            editor.putString("NAME", value)
            editor.commit()
        }

    var mobile_no: String
        get() = preferences.getString("NUMBER", "").toString()
        set(value) {
            editor.putString("NUMBER", value)
            editor.commit()
        }

    var location: String
        get() = preferences.getString("LOCATION", "").toString()
        set(value) {
            editor.putString("LOCATION", value)
            editor.commit()
        }

    fun clearData() {
        editor.clear()
        editor.commit()
    }
}