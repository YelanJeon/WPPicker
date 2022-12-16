package com.wppicker.common

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    private val BASE_URL = "https://api.unsplash.com/"
    private val API_KEY = "F5m_cg2MswY8Z6w2_u_1sHejZEJNdNtIrkXouVrTRus"
    private val client = OkHttpClient.Builder().addNetworkInterceptor(object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val newRequestBuilder = chain
                .request()
                .newBuilder()
                .addHeader("Authorization","Client-ID $API_KEY")
            val request = newRequestBuilder.build()
            val response = chain.proceed(request);
            Log.i("TEST", "url > ${request.url()}")
            Log.i("TEST", "response > ${response.code()}")
            return response
        }
    }).build()

    @Singleton
    @Provides
    fun createRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}