package com.wppicker.screen.main

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wppicker.data.*
import com.wppicker.request.ReqImage
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class MainRepository @Inject constructor (var retrofit: Retrofit) {
    fun loadTopics() : Flow<PagingData<TopicData>> {
        return Pager(PagingConfig(pageSize = 10, initialLoadSize = 10)) {
            TopicPagingSource(retrofit)
        }.flow
    }

    fun loadPhotos(topicIdx: String) : Flow<PagingData<PhotoData>>{
        return Pager(PagingConfig(pageSize = 10, initialLoadSize = 10)) {
            PhotoPagingSource(topicIdx, retrofit)
        }.flow
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

interface OnPhotoListLoaded {
    fun onPhotoLoaded(photoList: List<PhotoData>)
}