package com.happyhappyyay.android.dreamdiary.journal

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class SnapPageListener (private val snapHelper: SnapHelper,
                        val action: (position:Int)->Unit)
    : RecyclerView.OnScrollListener() {
    var position = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if(newState == RecyclerView.SCROLL_STATE_IDLE){
            updatePosition(recyclerView)
        }
    }

    private fun updatePosition(recyclerView: RecyclerView) {
        val snapPos = snapHelper.getSnapPosition(recyclerView)
        if(snapPos != position){
            action(snapPos)
            position = snapPos
        }
    }
}