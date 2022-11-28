package com.wppicker.screen.main

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wppicker.R
import com.wppicker.data.TopicData
import com.wppicker.common.Utils
import com.wppicker.widget.RoundedCornerImageView

class TopicAdapter(): RecyclerView.Adapter<TopicHolder>() {
    var dataList: List<TopicData>? = null
    var topicClickListener: OnTopicClickListener? = null

    var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicHolder {
        val holder = TopicHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_topic, null))
        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            val newPosition = holder.adapterPosition

            if(oldPosition != newPosition) {
                selectedPosition = newPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(newPosition)
                topicClickListener?.onTopicClicked(dataList!![selectedPosition])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: TopicHolder, position: Int) {
        holder.bind(dataList!![position], selectedPosition == position)
    }

    override fun getItemCount(): Int {
        return if(dataList == null) 0 else dataList!!.size
    }

    fun getSelectedItem(): TopicData {
        return dataList!![selectedPosition]
    }

}

class TopicHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val ivIcon = itemView.findViewById<RoundedCornerImageView>(R.id.iv_topic)
    val tvName = itemView.findViewById<TextView>(R.id.tv_topic)

    fun bind(data: TopicData, isSelected: Boolean) {
        if(data.idx == TopicData.TOPIC_IDX_ALL) {
            Glide.with(itemView.context).load(R.drawable.img_topic_all).centerCrop().into(ivIcon)
        }else{
            Glide.with(itemView.context).load(data.getIconURL()).centerCrop().into(ivIcon)
        }

        tvName.text = data.name
        if(isSelected) {
        val primaryColor = ResourcesCompat.getColor(itemView.resources, R.color.primary, null)
            ivIcon.setStroke(primaryColor, Utils.dimensionToPixel(itemView.context, 3f))
            tvName.typeface = Typeface.DEFAULT_BOLD
            tvName.setTextColor(primaryColor)
        }else{
            val lineColor = ResourcesCompat.getColor(itemView.resources, R.color.line, null)
            ivIcon.setStroke(lineColor, Utils.dimensionToPixel(itemView.context, 1f))
            tvName.typeface = Typeface.DEFAULT
            tvName.setTextColor(0xFF000000.toInt())
        }

    }
}

interface OnTopicClickListener {
    fun onTopicClicked(topicData: TopicData)
}
