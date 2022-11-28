package com.wppicker.common

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyWallpaperManager {
    companion object {
        fun showSettingDialog(context: Context, bitmap: Bitmap, message: String) {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Random image will setting")
            builder.setItems(arrayOf("Home screen", "Lock screen", "Home screen and lock screen")
            ) { dialog, which ->
                val manager = WallpaperManager.getInstance(context)
                when(which) {
                    0 -> {
                        manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    }
                    1 -> {
                        manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    }
                    2 -> {
                        manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                        manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    }
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            builder.create().show()
        }
    }
}