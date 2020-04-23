package com.happyhappyyay.android.dreamdiary.journal

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.happyhappyyay.android.dreamdiary.database.Page
import com.happyhappyyay.android.dreamdiary.database.startOfDay
import com.happyhappyyay.android.dreamdiary.databinding.ListItemPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class JournalPageAdapter(private val viewModel: JournalViewModel):
    ListAdapter<Page, JournalPageAdapter.ViewHolder>(PageDiffCallback()) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val viewPool = RecyclerView.RecycledViewPool()
    init{
        viewPool.setMaxRecycledViews(0,3)
    }

    fun addTodayAndSubmitList(list: List<Page>?) {
        adapterScope.launch {
            lateinit var pages: List<Page>
            if(list !== null && list.isNotEmpty()) {
                val size = list.size-1
                val today = startOfDay()
                pages= list
                for (i in size downTo 0) {
                    Log.d("JournalPageAdapter","${list[i].date.time} compared to ${today.time}")
                    if (list[i].containsDate(today))break
                    if (list[i].date.before(today)) {
                        Log.d("JournalPageAdapter","entry: ${list[i].date} today: ${today.toString()}")
                        val newList = list.toMutableList()
                        newList.add(Page())
                        pages = newList
                        break
                    }
                }
            }
            else{
                pages = listOf(Page())
            }

            withContext(Dispatchers.Main) {
                Log.d("journalPageAdapter","entries: ${pages.size}")
                submitList(pages)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val page = getItem(position)
        holder.bind(viewPool,viewModel, page)
    }

    class ViewHolder private constructor(private val binding: ListItemPageBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPageBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, parent.context)
            }
        }

        fun bind(viewPool: RecyclerView.RecycledViewPool, viewModel: JournalViewModel, page: Page){
            binding.dateText.text = page.date.toDayFormat()
            createDateListener(binding.dateText,page.date,viewModel)
            binding.recyclerViewEntry.setRecycledViewPool(viewPool)
            binding.recyclerViewEntry.adapter = JournalEntryAdapter(viewModel,page)
        }

        private fun createDateListener(textView: TextView, date:Date, viewModel: JournalViewModel){
            val cal = Calendar.getInstance()
            cal.timeInMillis = date.time
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.clear(Calendar.MINUTE)
                    cal.clear(Calendar.SECOND)
                    cal.clear(Calendar.MILLISECOND)
                    viewModel.goToDateOrCreate(date = Date(cal.timeInMillis))
                }
            textView.setOnClickListener {
                DatePickerDialog(
                    context,
                        dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }
}

fun Page.containsDate(date: Date): Boolean{
    return this.date.time == date.time
}

private class PageDiffCallback : DiffUtil.ItemCallback<Page>() {
    override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
        return oldItem.pageId == newItem.pageId
    }

    override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
        if(oldItem == newItem){
            if(oldItem.entries == newItem.entries){
                return false
            }
            return true
        }
        return false
    }
}

fun Date.toDayFormat():String{
    val format = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
    return format.format(this)
}

fun Date.toYearFormat():String{
    val format = SimpleDateFormat(", yyyy",Locale.US)
    return format.format(this)
}

fun String.toDateFormat():Date?{
    try{
        val date = SimpleDateFormat("MMMM dd, yyyy", Locale.US).parse(this)
        date?.let{
            return Date(it.time)
        }
    }
    catch (e: Exception){
        return null
    }
    return null
}
