package com.wppicker.common

import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue

class Utils {
    companion object {
        fun dimensionToPixel(context: Context, dp: Float) : Float {
            val pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
            return pixel
        }

        fun getScreenWidth(context: Context) : Int{
            return context.resources.displayMetrics.widthPixels
        }

        fun getScreenHeight(context: Context) : Int{
            return context.resources.displayMetrics.heightPixels
        }


    }

}
