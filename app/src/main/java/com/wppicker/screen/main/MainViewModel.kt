package com.wppicker.screen.main

import android.content.Intent
import androidx.lifecycle.*
import com.wppicker.data.PhotoData
import com.wppicker.data.TopicData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    var repository: MainRepository
): ViewModel() {

    private val _topics = MutableLiveData<List<TopicData>>()
    val topics = _topics as LiveData<List<TopicData>>

    private val _photos = MutableLiveData<List<PhotoData>>()
    val photos = _photos as LiveData<List<PhotoData>>

    private val _intent = MutableLiveData<Intent>()
    val intent = _intent as LiveData<Intent>

    fun loadTopics() {
        repository.loadTopics(object : OnTopicListLoaded {
            override fun onTopicLoaded(topicList: List<TopicData>) {
                _topics.value = topicList
            }
        })
    }

    fun loadPhotos(topicIdx: String) {
        repository.loadPhotos(topicIdx, object : OnPhotoListLoaded {
            override fun onPhotoLoaded(photoList: List<PhotoData>) {
                _photos.value = photoList
            }
        })
    }

    fun getRandomPhoto(topicIdx: String, callback: (String) -> Unit) {
        repository.loadRandomPhoto(topicIdx, object: OnPhotoListLoaded {
            override fun onPhotoLoaded(photoList: List<PhotoData>) {
                callback.invoke(photoList[0].photoIdx)
            }
        })
    }

    fun setSearchIntent(mIntent: Intent) {
        _intent.value = mIntent
    }

    class Factory @Inject constructor(private val repository: MainRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }

    }
}