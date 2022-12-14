package com.wppicker.screen.main

import com.wppicker.data.*
import com.wppicker.request.ReqImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class MainRepository @Inject constructor (var retrofit: Retrofit) {
    fun loadTopics(onTopicLoaded: OnTopicListLoaded) {
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

    fun loadPhotos(topicIdx: String, onPhotoLoaded: OnPhotoListLoaded) {
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

    fun loadRandomPhoto(topicIdx: String, onPhotoLoaded: OnPhotoListLoaded) {
        retrofit.create(ReqImage::class.java).getRandomPhoto(topicIdx).enqueue(object : Callback<PhotoData> {
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

interface OnTopicListLoaded {
    fun onTopicLoaded(topicList: List<TopicData>)
}

interface OnPhotoListLoaded {
    fun onPhotoLoaded(photoList: List<PhotoData>)
}