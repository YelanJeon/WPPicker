package com.wppicker.screen.search

import com.wppicker.data.PhotoData
import com.wppicker.data.SearchPhotoResultData
import com.wppicker.request.ReqImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class SearchRepository @Inject constructor (var retrofit: Retrofit) {
    fun searchPhoto(keyword: String, mOrientation: String?, onSearchPhoto: OnSearchPhoto) {
        retrofit.create(ReqImage::class.java).getSearchPhotoList(keyword, mOrientation).enqueue(object : Callback<SearchPhotoResultData> {
            override fun onResponse(call: Call<SearchPhotoResultData>, response: Response<SearchPhotoResultData>) {
                if(response.code() == 200) {
                    onSearchPhoto.onSearch(response.body()!!.resultList)
                }else{
                    onSearchPhoto.onSearch(listOf())
                }
            }

            override fun onFailure(call: Call<SearchPhotoResultData>, t: Throwable) {
                onSearchPhoto.onSearch(listOf())
            }

        })
    }
}

interface OnSearchPhoto {
    fun onSearch(photoList:List<PhotoData>)
}