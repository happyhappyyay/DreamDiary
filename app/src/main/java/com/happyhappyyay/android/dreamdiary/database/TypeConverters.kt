package com.happyhappyyay.android.dreamdiary.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Date
import kotlin.collections.ArrayList

class ListStringConverter{

    companion object {
        @TypeConverter
        @JvmStatic
        fun fromArrayList(list: ArrayList<String>): String {
            return Gson().toJson(list)
        }

        @TypeConverter
        @JvmStatic
        fun fromString(string: String): ArrayList<String> {
            return Gson().fromJson(string, object : TypeToken<ArrayList<String>>() {}.type)
        }
    }
}

class DateLongConverter{
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromDate(date: Date): Long {
            return date.time
        }

        @TypeConverter
        @JvmStatic
        fun fromLong(long: Long): Date {
            return Date(long)
        }
    }
}