package com.wppicker.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.wppicker.R

class RoundedCornerImageView: AppCompatImageView {
    val path = Path()
    var mStrokePaint: Paint? = null
    var radius: Float = 16f

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initialize(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedCornerImageView, 0, 0).apply {
            try{
                radius = getDimension(R.styleable.RoundedCornerImageView_android_radius, 0f)
                val strokeColor = getColor(R.styleable.RoundedCornerImageView_android_strokeColor, 0)
                val strokeWidth = getFloat(R.styleable.RoundedCornerImageView_android_strokeWidth, 0f)

                setStrokePaint(strokeColor, strokeWidth)

            }finally {
                recycle()
            }
        }
    }

    private fun setStrokePaint(strokeColor: Int, strokeWidth: Float) {
        mStrokePaint = Paint()
        mStrokePaint!!.apply {
            this.style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            this.color = strokeColor
            this.isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        with(canvas!!) {
            path.addRoundRect(0f, 0f, width.toFloat(), height.toFloat(), radius-1, radius-1, Path.Direction.CCW)
            clipPath(path)
        }
        super.onDraw(canvas)

        if(mStrokePaint != null) {
            canvas!!.drawRoundRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                radius,
                radius,
                mStrokePaint!!
            )
        }
    }

    fun setStroke(@ColorInt strokeColor: Int, strokeWidth: Float) {
        if(mStrokePaint == null) {
            setStrokePaint(strokeColor, strokeWidth)
            invalidate()
        }else{
            mStrokePaint!!.color = strokeColor
            mStrokePaint!!.strokeWidth = strokeWidth
            invalidate()
        }
    }

}