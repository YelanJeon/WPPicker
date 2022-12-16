package com.wppicker.screen.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wppicker.data.PhotoData
import com.wppicker.data.SearchPhotoResultData
import com.wppicker.request.ReqImage
import com.wppicker.screen.main.PhotoPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class SearchRepository @Inject constructor (var retrofit: Retrofit) {
    fun searchPhotos(keyword: String, mOrientation: String?) : Flow<PagingData<PhotoData>> {
        return Pager(PagingConfig(pageSize = 10, initialLoadSize = 10)) {
            SearchPagingSource(keyword, mOrientation, retrofit)
        }.flow
    }
}