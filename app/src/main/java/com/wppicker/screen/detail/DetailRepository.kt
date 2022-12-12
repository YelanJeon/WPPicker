package com.wppicker.screen.detail

import com.wppicker.common.MyRetrofit
import com.wppicker.data.AuthorData
import com.wppicker.data.PhotoData
import com.wppicker.data.PhotoURLS
import com.wppicker.request.ReqImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DetailRepository(private val retrofit: Retrofit) {

    fun loadPhoto(photoIdx: String, onPhotoLoaded: OnPhotoLoaded) {
        MyRetrofit.retrofit.create(ReqImage::class.java).getImageDetail(photoIdx).enqueue(object :
            Callback<PhotoData> {
            override fun onResponse(call: Call<PhotoData>, response: Response<PhotoData>) {
                onPhotoLoaded.onPhotoLoaded(response.body()?:getDummy())
            }

            override fun onFailure(call: Call<PhotoData>, t: Throwable) {
                onPhotoLoaded.onPhotoLoaded(getDummy())
            }

            private fun getDummy(): PhotoData {
                return PhotoData("", AuthorData("","",""), PhotoURLS("",""))
            }
        })
    }
}

interface OnPhotoLoaded {
    fun onPhotoLoaded(photo: PhotoData)
}