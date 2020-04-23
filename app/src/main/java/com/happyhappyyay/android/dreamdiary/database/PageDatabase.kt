package com.happyhappyyay.android.dreamdiary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Page::class], version = 1, exportSchema = false)
@TypeConverters(DateLongConverter::class, ListStringConverter::class)
abstract class PageDatabase : RoomDatabase() {

    abstract val pageDatabaseDao: PageDao

    companion object {

        @Volatile
        private var INSTANCE: PageDatabase? = null

        fun getInstance(context: Context): PageDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PageDatabase::class.java,
                        "journal_entry_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}