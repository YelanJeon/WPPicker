package com.wppicker.request

import com.wppicker.data.PhotoData
import com.wppicker.data.SearchPhotoResultData
import com.wppicker.data.TopicData
import retrofit2.Call
import retrofit2.http.*

interface ReqImage {

    @GET("/topics")
    fun getTopicList() : Call<List<TopicData>>

    @GET("/photos")
    fun getAllImageList() : Call<List<PhotoData>>

    @GET("/topics/{topicID}/photos")
    fun getImageList(
        @Path("topicID") topicIdx: String,
    ) : Call<List<PhotoData>>

    @GET("/photos/{photoID}")
    fun getImageDetail(
        @Path("photoID") photoIdx: String) : Call<PhotoData>

    @GET("/photos/random")
    fun getRandomPhoto() : Call<PhotoData>

    @GET("/search/photos")
    fun getSearchPhotoList(
        @Query("query") keyword: String,
        @Query("orientation") orientation: String? = null
    ) : Call<SearchPhotoResultData>

}
