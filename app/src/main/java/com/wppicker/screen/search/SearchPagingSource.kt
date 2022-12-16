package com.wppicker.screen.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wppicker.data.PhotoData
import com.wppicker.data.SearchPhotoResultData
import com.wppicker.data.TopicData
import com.wppicker.request.ReqImage
import retrofit2.*

class SearchPagingSource(
    private val keyword: String,
    private val orientation: String?,
    private val retrofit: Retrofit
    ) : PagingSource<Int, PhotoData>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoData> {
        return try {
            val currentPage = params.key ?: 1

            val response = retrofit.create(ReqImage::class.java).getSearchPhotoList(keyword, orientation).awaitResponse()
            val photoList = response.body()!!.resultList
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
