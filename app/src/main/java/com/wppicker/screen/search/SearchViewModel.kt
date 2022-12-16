package com.wppicker.screen.search

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wppicker.data.PhotoData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    var repository: SearchRepository
) : ViewModel() {

    var photoFlow : Flow<PagingData<PhotoData>>? = null

    val _keyword = MutableLiveData<String>()
    val keyword = _keyword as LiveData<String>

    val _orientation = MutableLiveData<String>()
    val orientation = _orientation as LiveData<String>

    fun setKeyword(keyword: String) {
        _keyword.value = keyword
    }

    fun setOrientation(orientation: String) {
        _orientation.value = orientation
    }

    fun searchPhotos(): Flow<PagingData<PhotoData>> {
        val mOrientation = if(_orientation.value == "All") null else _orientation.value?.lowercase()
        val mKeyword = _keyword.value ?: ""
        val newResult = repository.searchPhotos(mKeyword, mOrientation).cachedIn(viewModelScope)
        photoFlow = newResult
        return newResult

    }
    class Factory @Inject constructor(private val repository: SearchRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(repository) as T
        }
    }
}

