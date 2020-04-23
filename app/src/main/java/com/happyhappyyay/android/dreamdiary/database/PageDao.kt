package com.happyhappyyay.android.dreamdiary.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.sql.Date

@Dao
interface PageDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(page: Page)

    @Update
    fun update(page: Page)

    @Query ("SELECT * FROM page_table ORDER BY date")
    fun getAllPages(): LiveData<List<Page>>

    @Query("SELECT * FROM page_table WHERE date = :date")
    fun getToday(date: Date = startOfDay()): Page?

    @Query ("SELECT * FROM page_table WHERE pageId = :pageId LIMIT 1")
    fun getPage(pageId: Long):Page?

    @Query ("DELETE FROM page_table WHERE pageId = :pageId")
    fun deletePage(pageId: Long)

    @Query("DELETE FROM page_table")
    fun deleteAll()
}