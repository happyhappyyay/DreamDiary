package com.happyhappyyay.android.dreamdiary.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.happyhappyyay.android.dreamdiary.database.Page
import com.happyhappyyay.android.dreamdiary.database.startOfDay
import com.happyhappyyay.android.dreamdiary.journal.toDayFormat
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun generateNoteOnSD(
    pages: List<Page>?,context:Context
) {

    if(pages != null) {
        val stringBuilder = StringBuilder()
        for (i in pages.indices) {
            val page = pages[i]
            val header = page.date.toDayFormat()
            val mStringBuilder = StringBuilder()
            for(j in page.entries.indices){
                val entryItem = "[${j+1}]${page.entries[j]}"
                mStringBuilder.append(entryItem)
            }
            val body = "$header ${System.lineSeparator()}${System.lineSeparator()} $mStringBuilder" +
                    "${System.lineSeparator()}${System.lineSeparator()}"
            stringBuilder.append(body)
        }

        try {
            val root = File(Environment.getExternalStorageDirectory(), "DreamDiary")
            if (!root.exists()) {
                root.mkdirs()
            }
            val format = SimpleDateFormat("MM-dd-yy", Locale.US)
            val titleDate = format.format(startOfDay())
            val fileName = File(root, "diary_to_${titleDate}.txt")
            val writer = FileWriter(fileName)
            writer.append(stringBuilder.toString())
            writer.flush()
            writer.close()
            Toast.makeText(context, "File written to storage in DreamDiary/diary_to_${titleDate}.txt", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}