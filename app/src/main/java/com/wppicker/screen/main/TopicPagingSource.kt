package com.wppicker.screen.main

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wppicker.data.TopicData
import com.wppicker.data.TopicPhotoURL
import com.wppicker.data.TopicPhotoURLS
import com.wppicker.request.ReqImage
import retrofit2.Retrofit
import retrofit2.awaitResponse

class TopicPagingSource(private val retrofit: Retrofit) : PagingSource<Int, TopicData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopicData> {
        return try {
            val currentPage = params.key ?: 1

            val response = retrofit.create(ReqImage::class.java).getTopicList(page = currentPage, count = params.loadSize).awaitResponse()

            val topicList = mutableListOf<TopicData>()

            topicList.addAll(response.body()!!)

            if(currentPage == 1) {
                val topicAll = TopicData(
                    TopicData.TOPIC_IDX_ALL, "All Topics", TopicPhotoURLS(
                        TopicPhotoURL("")
                    )
                )
                topicList.add(0, topicAll)
            }

            val prevKey = if(currentPage == 1) null else currentPage-1

            val nextKey = if(params.loadSize > response.body()!!.size) null else currentPage + 1

            LoadResult.Page(
                data = topicList,
                prevKey = prevKey,
                nextKey = nextKey
            )
        }catch(e: java.lang.Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TopicData>): Int? {
        return state.anchorPosition?.let {
            anchorPosition ->
                state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}
