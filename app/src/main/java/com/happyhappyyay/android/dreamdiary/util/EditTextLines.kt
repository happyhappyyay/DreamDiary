package com.happyhappyyay.android.dreamdiary.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import kotlin.math.roundToInt


class EditTextLines : androidx.appcompat.widget.AppCompatEditText {
    private val mPaint: Paint = Paint()

    constructor(context: Context?) : super(context) {
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initPaint()
    }

    private fun initPaint() {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = -0x80000000
        mPaint.strokeWidth = 5.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        val left = left
        val right = right
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val height = height
        val lineHeight = lineHeight * lineSpacingMultiplier
        val count = (height - paddingTop - paddingBottom) / lineHeight
        for (i in 0 until count.roundToInt()) {
            val baseline = lineHeight * (i + 1.25) + paddingTop
            canvas.drawLine((left + paddingLeft).toFloat(),
                baseline.toFloat(), (right - paddingRight).toFloat(), baseline.toFloat(), mPaint)
        }
        super.onDraw(canvas)
    }
}