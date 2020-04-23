package com.happyhappyyay.android.dreamdiary.password

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.happyhappyyay.android.dreamdiary.R

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val app: Application = getApplication()
    private val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    fun hasPassword():Boolean{
        if(!sharedPrefs.getBoolean(app.getString(R.string.pref_key_require_pass),false) ||
            "" == sharedPrefs.getString(app.getString(R.string.pref_key_password),"")){
            Log.d("PasswordViewModel", "false")
            return false
        }
        return true
    }
    fun getPassword():String{
        return sharedPrefs.getString(app.getString(R.string.pref_key_password),"")?: ""
    }
}
