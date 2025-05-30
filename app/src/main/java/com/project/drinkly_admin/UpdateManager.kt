package com.project.drinkly_admin

import android.content.Context
import android.content.SharedPreferences

class UpdateManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UpdatePrefs", Context.MODE_PRIVATE)

    fun saveIsAvailable(isAvailable: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAvailable", isAvailable)
        editor.apply()
    }

    fun getIsAvailable(): Boolean {
        return sharedPreferences.getBoolean("isAvailable", false)
    }
}