package com.daniel.personalapplication.shared_pref

import android.content.Context
import android.content.SharedPreferences

class MySession(context: Context) {

    private var myPref: SharedPreferences =
        context.getSharedPreferences("my_session", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = myPref.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun getData(key: String): String? {
        return myPref.getString(key, "-")
    }
}