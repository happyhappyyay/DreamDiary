package com.happyhappyyay.android.dreamdiary.journal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar
import com.happyhappyyay.android.dreamdiary.R
import com.happyhappyyay.android.dreamdiary.database.PageDatabase
import com.happyhappyyay.android.dreamdiary.databinding.JournalFragmentBinding


class JournalFrag : Fragment() {
    private lateinit var viewModel: JournalViewModel
    private lateinit var image: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<JournalFragmentBinding>(
            inflater,
            R.layout.journal_fragment,
            container,
            false
        )
        val application = requireNotNull(this.activity).application
        val database = PageDatabase.getInstance(application).pageDatabaseDao
        val factory = JournalViewModelFactory(application, database)
        viewModel = ViewModelProvider(this, factory).get(JournalViewModel::class.java)
        val adapter = JournalPageAdapter(viewModel)
        val recyclerView = binding.recyclerViewDay
        recyclerView.adapter = adapter
        image = binding.bookImage
        val snapHelper = PagerSnapHelper()
        val snackbar = Snackbar.make(
            activity!!.findViewById(android.R.id.content),
            "",
            Snackbar.LENGTH_LONG
        )
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).textAlignment =
            View.TEXT_ALIGNMENT_CENTER
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            backNavOption()
        }
        snapHelper.attachToRecyclerView(recyclerView)
        binding.lifecycleOwner = this
        viewModel.pages.observe(viewLifecycleOwner, Observer {
            Log.d("JournalFrag", "pagesUpdate")

            it?.let {
                Log.d("JournalFrag", "submitList")
                adapter.addTodayAndSubmitList(it)
                setRecyclerPositionWithEntry(recyclerView)
            }
        })
        viewModel.text.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("JournalFrag", "inside $it")
                viewModel.containsInstructions(it, snapHelper.getSnapPosition(recyclerView))
            }
        })
        viewModel.isRecording.observe(viewLifecycleOwner, Observer {
            if (it) {
                clearFocus()
                viewModel.startRecording()
            }
        })
        viewModel.pageNumber.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it != snapHelper.getSnapPosition(recyclerView)) {
                    Log.d("JournalFrag", "$it")
                    recyclerView.smoothScrollToPosition(it)
                }
            }
        })
        viewModel.snackBarText.observe(viewLifecycleOwner, Observer {
            it?.let{
                snackbar.setText(it)
                snackbar.show()
                Log.d("JournalFrag","snackbar")
                viewModel.clearSnackBar()
            }
        })
        val snapPageListener =
            SnapPageListener(snapHelper) { pos -> viewModel.pageNumber.value = pos }
        binding.recyclerViewDay.addOnScrollListener(snapPageListener)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onPause() {
        clearFocus()
        super.onPause()
    }

    private fun clearFocus() {
        image.requestFocus()
    }

    private fun setRecyclerPositionWithEntry(recyclerView: RecyclerView) {
        val num = viewModel.pageNumber.value
        Log.d("JournalFrag", "setRecycler $num")
        if (num != null) {
            recyclerView.smoothScrollToPosition(num)
        } else {
            viewModel.pages.value?.size?.let {
                viewModel.setPageNumber(it)
            }
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this.activity!!,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val recordRequestCode = 199
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                recordRequestCode
            )
        }
    }

    private fun backNavOption(){
        if(viewModel.hasPassword()){
            clearFocus()
            findNavController().navigateUp()
        }
        else{
            activity!!.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.helpFrag -> this.findNavController()
                .navigate(JournalFragDirections.actionJournalFragToHelpFrag())
            R.id.settings -> this.findNavController()
                .navigate(JournalFragDirections.actionJournalFragToSettings())
            R.id.microphone_record -> startRecording()
            else -> backNavOption()
        }
        return true
    }

    private fun startRecording() {
        Log.d("JournalFrag", "microphone selected")
        if (viewModel.isRecording.value == false || viewModel.isRecording.value == null) {
            requestPermission()
            viewModel.setIsRecording(true)
        }
    }
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}
