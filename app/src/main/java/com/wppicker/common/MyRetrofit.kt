package com.wppicker.common

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyRetrofit {
    private const val BASE_URL = "https://api.unsplash.com/"
    private const val API_KEY = "F5m_cg2MswY8Z6w2_u_1sHejZEJNdNtIrkXouVrTRus"

    private val client = OkHttpClient.Builder().addNetworkInterceptor(object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val newRequestBuilder = chain
                .request()
                .newBuilder()
                .addHeader("Authorization","Client-ID $API_KEY")
                .addHeader("Accept-Version", "v1")
            val request = newRequestBuilder.build()
            Log.i("TEST", "url : ${request.url()}")
            return chain.proceed(request)
        }
    }).build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}