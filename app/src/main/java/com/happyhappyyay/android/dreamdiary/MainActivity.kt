package com.happyhappyyay.android.dreamdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.happyhappyyay.android.dreamdiary.databinding.ActivityMainBinding
import com.happyhappyyay.android.dreamdiary.util.createThemeReference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.pref_key_theme),"0")
        val book = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.pref_key_book_color),"0")
        setTheme(createThemeReference(book,theme))
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this,navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp()
    }
}
