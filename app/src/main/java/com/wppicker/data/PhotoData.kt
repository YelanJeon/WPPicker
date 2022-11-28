package com.wppicker.data

import com.google.gson.annotations.SerializedName

data class PhotoData(
    @SerializedName("id") val photoIdx: String,
    @SerializedName("user") val author: AuthorData,
    @SerializedName("urls") val urls: PhotoURLS
)

data class AuthorData(
    @SerializedName("id") val authorIdx: String,
    @SerializedName("username") val authorName: String,
    @SerializedName("portfolio_url") val portfolioURL: String
)

data class PhotoURLS (
    @SerializedName("full") val fullImageURL: String,
    @SerializedName("regular") val thumbnailURL: String
)
