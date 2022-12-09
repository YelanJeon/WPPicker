package com.wppicker.screen.main

import android.view.View
import android.widget.Toast
import com.wppicker.common.MyRetrofit
import com.wppicker.data.*
import com.wppicker.request.ReqImage
import com.wppicker.screen.detail.DetailDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class MainRepository(private val retrofit: Retrofit) {
    fun loadTopics(onTopicLoaded: OnTopicLoaded) {
        var defaultList = listOf(
            TopicData(
                TopicData.TOPIC_IDX_ALL, "All Topics", TopicPhotoURLS(
                    TopicPhotoURL("")
                )
            )
        )
        retrofit.create(ReqImage::class.java).getTopicList().enqueue(object:
            Callback<List<TopicData>> {
            override fun onResponse(call: Call<List<TopicData>>, response: Response<List<TopicData>>) {
                var list = defaultList
                if(response.code() == 200) {
                    list = list + response.body()!!
                }
                onTopicLoaded.onTopicLoaded(list)
            }

            override fun onFailure(call: Call<List<TopicData>>, t: Throwable) {
                onTopicLoaded.onTopicLoaded(defaultList)
            }
        })
    }

    fun loadPhotos(topicIdx: String, onPhotoLoaded: OnPhotoLoaded) {
        val callback = object : Callback<List<PhotoData>> {
            override fun onResponse(call: Call<List<PhotoData>>, response: Response<List<PhotoData>>) {
                val list = mutableListOf<PhotoData>()
                if(response.code() == 200) {
                    response.body()?.let { list.addAll(it) }
                }
                onPhotoLoaded.onPhotoLoaded(list)
            }

            override fun onFailure(call: Call<List<PhotoData>>, t: Throwable) {
                onPhotoLoaded.onPhotoLoaded(mutableListOf<PhotoData>())
            }
        }

        if(topicIdx == TopicData.TOPIC_IDX_ALL) {
            retrofit.create(ReqImage::class.java).getAllImageList().enqueue(callback)
        }else{
            retrofit.create(ReqImage::class.java).getImageList(topicIdx).enqueue(callback)
        }
    }

    fun loadRandomPhoto(topicIdx: String, onPhotoLoaded: OnPhotoLoaded) {
        MyRetrofit.retrofit.create(ReqImage::class.java).getRandomPhoto().enqueue(object : Callback<PhotoData> {
            override fun onResponse(call: Call<PhotoData>, response: Response<PhotoData>) {
                if(response.body() == null) {
                    onPhotoLoaded.onPhotoLoaded(listOf(getDummyData()))
                }else{
                    onPhotoLoaded.onPhotoLoaded(listOf(response.body()!!))
                }
            }

            override fun onFailure(call: Call<PhotoData>, t: Throwable) {
                onPhotoLoaded.onPhotoLoaded(listOf(getDummyData()))
            }

            private fun getDummyData(): PhotoData {
                return PhotoData("", AuthorData("","",""), PhotoURLS("",""))
            }
        })
    }

}

interface OnTopicLoaded {
    fun onTopicLoaded(topicList: List<TopicData>)
}

interface OnPhotoLoaded {
    fun onPhotoLoaded(photoList: List<PhotoData>)
}