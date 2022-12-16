package com.wppicker.screen.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wppicker.R
import com.wppicker.common.Utils
import com.wppicker.data.PhotoData
import com.wppicker.screen.detail.DetailDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoListAdapter(): PagingDataAdapter<PhotoData, PhotoListHolder>(diffCallback) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<PhotoData>() {
            override fun areItemsTheSame(oldItem: PhotoData, newItem: PhotoData): Boolean {
                return oldItem.photoIdx == newItem.photoIdx
            }

            override fun areContentsTheSame(oldItem: PhotoData, newItem: PhotoData): Boolean {
                return oldItem.photoIdx == newItem.photoIdx
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.item_main_list, parent, false)
        val layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        rootView.layoutParams = layoutParams

        val holder = PhotoListHolder(rootView)
        holder.itemView.setOnClickListener {
            val dialog = DetailDialog.getInstance(getItem(holder.layoutPosition)!!.photoIdx)
            dialog.show((parent.context as AppCompatActivity).supportFragmentManager, "detail")
        }
        return holder
    }

    override fun onBindViewHolder(holder: PhotoListHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

}

class PhotoListHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val ivImage = itemView.findViewById<ImageView>(R.id.iv_list)

    fun bind(data: PhotoData) {
        Glide.with(itemView.context).asBitmap().load(data.urls.thumbnailURL).into(object: CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                CoroutineScope(Dispatchers.Default).launch {
                    val imageWidth = (Utils.getScreenWidth(itemView.context) - Utils.dimensionToPixel(itemView.context, 20f)) /2
                    val newSize = getNewSize(resource.width, resource.height, imageWidth.toInt())
                    val newBitmap = Bitmap.createScaledBitmap(resource, newSize[0], newSize[1], true)
                    CoroutineScope(Dispatchers.Main).launch {
                        ivImage.setImageBitmap(newBitmap)
                    }
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                placeholder?.let {
                    ivImage.setImageDrawable(placeholder)
                }
            }

            private fun getNewSize(resourceWidth: Int, resourceHeight: Int, viewWidth: Int): Array<Int> {
                var newWidth = 0
                var newHeight = 0

                    newWidth = viewWidth
                    newHeight = newWidth * resourceHeight / resourceWidth

                return arrayOf(newWidth, newHeight)
            }

        })
    }


}