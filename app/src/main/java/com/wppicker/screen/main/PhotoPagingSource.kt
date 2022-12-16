package com.wppicker.screen.main

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wppicker.data.PhotoData
import com.wppicker.data.TopicData
import com.wppicker.request.ReqImage
import retrofit2.Retrofit
import retrofit2.awaitResponse

class PhotoPagingSource(
    private val topicIdx: String,
    private val retrofit: Retrofit
) : PagingSource<Int, PhotoData>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoData> {
        return try {
            val currentPage = params.key ?: 1

            val response = if(topicIdx == TopicData.TOPIC_IDX_ALL) {
                               retrofit.create(ReqImage::class.java).getAllImageList(page = currentPage, count = params.loadSize).awaitResponse()
                           }else{
                               retrofit.create(ReqImage::class.java).getImageList(topicIdx = topicIdx, page = currentPage, count = params.loadSize).awaitResponse()
                           }

            val photoList = response.body()!!
            val prevKey = if(currentPage == 1) null else currentPage-1
            val nextKey = if(params.loadSize > photoList.size) null else currentPage + 1

            LoadResult.Page(
                data = photoList,
                prevKey = prevKey,
                nextKey = nextKey
            )
        }catch(e: java.lang.Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoData>): Int? {
        return state.anchorPosition?.let {
                anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
