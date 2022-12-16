package com.wppicker.screen.main

import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wppicker.data.PhotoData
import com.wppicker.data.TopicData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    var repository: MainRepository
): ViewModel() {
    var topicFlow : Flow<PagingData<TopicData>>? = null
    var photoFlow : Flow<PagingData<PhotoData>>? = null

    val _selectedTopicPosition = MutableLiveData<Int>()
    val selectedTopicPosition = _selectedTopicPosition as LiveData<Int>

    fun loadTopicList(): Flow<PagingData<TopicData>> {
        val newResult = repository.loadTopics().cachedIn(viewModelScope)
        topicFlow = newResult
        return newResult
    }

    fun loadPhotoList(topicIdx: String): Flow<PagingData<PhotoData>> {
        val newResult = repository.loadPhotos(topicIdx).cachedIn(viewModelScope)
        photoFlow = newResult
        return newResult
    }

    fun getRandomPhoto(topicIdx: String, callback: (String) -> Unit) {
        repository.loadRandomPhoto(topicIdx, object: OnPhotoListLoaded {
            override fun onPhotoLoaded(photoList: List<PhotoData>) {
                callback.invoke(photoList[0].photoIdx)
            }
        })
    }

    fun setSelectedTopicPosition(selectedPosition: Int) {
        _selectedTopicPosition.value = selectedPosition
    }

    class Factory @Inject constructor(private val repository: MainRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }

    }
}