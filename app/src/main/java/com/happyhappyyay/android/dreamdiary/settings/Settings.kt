package com.happyhappyyay.android.dreamdiary.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.happyhappyyay.android.dreamdiary.R
import com.happyhappyyay.android.dreamdiary.database.PageDatabase
import com.happyhappyyay.android.dreamdiary.util.generateNoteOnSD


class Settings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d("Settings","created prefs")
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val themePreference: Preference? = findPreference(getString(R.string.pref_key_theme))
        themePreference?.setOnPreferenceChangeListener { preference, newValue ->
            val oldVal = preference.sharedPreferences.getString(getString(R.string.pref_key_theme),"0")
            Log.d("Settings"," old $oldVal new $newValue")
            if(oldVal != newValue){
                activity?.recreate()
            }
            true
        }

        val bookPreference: Preference? = findPreference(getString(R.string.pref_key_book_color))
        bookPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val oldVal = preference.sharedPreferences.getString(getString(R.string.pref_key_book_color),"0")
            Log.d("Settings"," oldb $oldVal newb $newValue")

            if(oldVal != newValue){
                activity?.recreate()
            }
            true
        }

        val exportPreference:Preference? = findPreference(getString(R.string.pref_key_export))
        exportPreference?.setOnPreferenceClickListener {
            context?.let {
                val database = PageDatabase.getInstance(context!!).pageDatabaseDao
                val pages = database.getAllPages()
                pages.observe(viewLifecycleOwner, Observer {
                    requestPermission()
                    generateNoteOnSD(pages.value,context!!) })
            }
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Settings","created view")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this.activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val storageRequestCode = 299
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                storageRequestCode
            )
        }
    }
}