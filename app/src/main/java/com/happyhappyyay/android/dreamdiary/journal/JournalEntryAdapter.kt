package com.happyhappyyay.android.dreamdiary.journal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyhappyyay.android.dreamdiary.database.Page
import com.happyhappyyay.android.dreamdiary.databinding.ListItemEntryBinding

class JournalEntryAdapter(private val viewModel: JournalViewModel, private val page: Page) :
    RecyclerView.Adapter<JournalEntryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel,page,position)
    }

    override fun getItemCount(): Int {
        return page.entries.size + 1
    }

    class ViewHolder private constructor(private val binding: ListItemEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemEntryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(viewModel: JournalViewModel, page:Page, position: Int){
//            val entryNumber = "[${position+1}]"
//            val string = if(page.entries.size == position) "$entryNumber " else "$entryNumber ${page.entries[position]}"
//            binding.entryEdit.setText(string)
            binding.position = position
            binding.viewModel = viewModel
            binding.page = page
        }
    }
}