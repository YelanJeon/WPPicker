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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    var repository: MainRepository
): ViewModel() {
    var _topicFlow = MutableStateFlow<PagingData<TopicData>?>(null)
    val topicFlow = _topicFlow as StateFlow<PagingData<TopicData>>
    var topicJob: Job? = null

    var _photoFlow = MutableStateFlow<PagingData<PhotoData>?>(null)
    val photoFlow = _photoFlow as StateFlow<PagingData<PhotoData>>
    var photoJob: Job? = null

    val _selectedTopicPosition = MutableLiveData<Int>()
    val selectedTopicPosition = _selectedTopicPosition as LiveData<Int>

    fun loadTopicList() {
        topicJob?.cancel()
        topicJob = viewModelScope.launch {
            repository.loadTopics().cachedIn(viewModelScope).collect {
                _topicFlow.value = it
            }
        }
    }

    fun loadPhotoList(topicIdx: String) {
        photoJob?.cancel()
        photoJob = viewModelScope.launch {
            repository.loadPhotos(topicIdx).cachedIn(viewModelScope).collect {
                _photoFlow.value = it
            }
        }
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