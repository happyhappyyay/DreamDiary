package com.happyhappyyay.android.dreamdiary.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "page_table")
data class Page(
    @PrimaryKey(autoGenerate = true)
    var pageId: Long = 0,
    val date:Date = startOfDay(),
    val entries: ArrayList<String> = ArrayList()
)

fun startOfDay():Date{
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)
    return Date(cal.timeInMillis)
}