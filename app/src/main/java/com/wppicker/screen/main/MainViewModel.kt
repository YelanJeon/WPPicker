package com.wppicker.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wppicker.data.PhotoData
import com.wppicker.data.TopicData
import retrofit2.Retrofit

class MainViewModel(
    val repository: MainRepository
): ViewModel() {
    private val _topics = MutableLiveData<List<TopicData>>()

    val topics = _topics as LiveData<List<TopicData>>
    private val _photos = MutableLiveData<List<PhotoData>>()

    val photos = _photos as LiveData<List<PhotoData>>
    var _randomPhotoIdx = MutableLiveData<String>()

    val randomPhotoIdx = _randomPhotoIdx as LiveData<String>
    fun loadTopics() {
        repository.loadTopics(object : OnTopicLoaded {
            override fun onTopicLoaded(topicList: List<TopicData>) {
                _topics.value = topicList
            }
        })
    }

    fun loadPhotos(topicIdx: String) {
        repository.loadPhotos(topicIdx, object : OnPhotoLoaded {
            override fun onPhotoLoaded(photoList: List<PhotoData>) {
                _photos.value = photoList
            }
        })
    }

    fun getRandomPhoto(topicIdx: String) {
        repository.loadRandomPhoto(topicIdx, object: OnPhotoLoaded {
            override fun onPhotoLoaded(photoList: List<PhotoData>) {
                _randomPhotoIdx.value = photoList[0].photoIdx
            }
        })
    }

    class Factory(val repository: MainRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }

    }
}

