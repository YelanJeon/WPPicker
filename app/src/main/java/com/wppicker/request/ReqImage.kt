package com.wppicker.request

import com.wppicker.data.PhotoData
import com.wppicker.data.SearchPhotoResultData
import com.wppicker.data.TopicData
import retrofit2.Call
import retrofit2.http.*

interface ReqImage {

    @GET("/topics")
    fun getTopicList(
        @Query("page") page: Int = 1,
        @Query("per_page") count: Int = 10
    ) : Call<List<TopicData>>

    @GET("/photos")
    fun getAllImageList(
        @Query("page") page: Int = 1,
        @Query("per_page") count: Int = 10
    ) : Call<List<PhotoData>>

    @GET("/topics/{topicID}/photos")
    fun getImageList(
        @Path("topicID") topicIdx: String,
        @Query("page") page: Int = 1,
        @Query("per_page") count: Int = 10
    ) : Call<List<PhotoData>>

    @GET("/photos/{photoID}")
    fun getImageDetail(
        @Path("photoID") photoIdx: String) : Call<PhotoData>

    @GET("/photos/random/")
    fun getRandomPhoto(
        @Query("topics") topicIdx: String
    ) : Call<PhotoData>

    @GET("/search/photos")
    fun getSearchPhotoList(
        @Query("query") keyword: String,
        @Query("orientation") orientation: String? = null
    ) : Call<SearchPhotoResultData>

}
