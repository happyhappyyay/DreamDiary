package com.happyhappyyay.android.dreamdiary.util

import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.happyhappyyay.android.dreamdiary.database.Page
import com.happyhappyyay.android.dreamdiary.journal.JournalViewModel

@BindingAdapter(value = ["viewModel","position","page"])
fun EditText.initiateListeners(viewModel: JournalViewModel, position:Int, page: Page?) {
    Log.d("BindingUtils","new focus")
    this.onFocusChangeListener = View.OnFocusChangeListener { view, isFocused ->
        if (!isFocused) {
            if (null != page) {
                if (view is EditText) {
                    viewModel.updateEntryInformation(page, position, this.text.toString())
                }
            }
            val fullEntry = "[${position+1}] ${this.text}"
            this.setText(fullEntry)
        }
        else{
            this.setText(text.substring("[${position+1}] ".length))
        }
    }
    this.setOnCreateContextMenuListener { contextMenu, _, _ ->
        contextMenu.add("Delete Entry").setOnMenuItemClickListener {
            if(page != null) viewModel.deleteEntryInformation(page,position)
            true
        }
        contextMenu.add("Delete All Entries").setOnMenuItemClickListener {
            if (page != null) viewModel.deleteAllEntryInformation(page)
            true
        }
        contextMenu.add("Delete Page").setOnMenuItemClickListener {
            if (page != null) viewModel.deletePage(page)
            true
        }
    }
}
