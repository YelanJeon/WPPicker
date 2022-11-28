package com.wppicker.data

import com.google.gson.annotations.SerializedName

data class SearchPhotoResultData(
    @SerializedName("results") val resultList : List<PhotoData>
)
