package com.happyhappyyay.android.dreamdiary.journal

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.happyhappyyay.android.dreamdiary.database.PageDao

@Suppress("UNCHECKED_CAST")
class JournalViewModelFactory (private val application:Application, private val database:PageDao)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            return JournalViewModel(application,database) as T
        }
            throw IllegalArgumentException("ViewModel Not Found")
    }
}