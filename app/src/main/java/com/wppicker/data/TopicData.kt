package com.wppicker.data

import com.google.gson.annotations.SerializedName

data class TopicData(
    @SerializedName("id") val idx: String,
    @SerializedName("title") val name: String,
    @SerializedName("cover_photo") private val iconURL: TopicPhotoURLS) {
    companion object {
        const val TOPIC_IDX_ALL = "ALL"
    }

    fun getIconURL(): String {
        return iconURL.urls.url
    }
}

class TopicPhotoURLS(@SerializedName("urls") val urls: TopicPhotoURL)

class TopicPhotoURL(@SerializedName("small") val url: String)
