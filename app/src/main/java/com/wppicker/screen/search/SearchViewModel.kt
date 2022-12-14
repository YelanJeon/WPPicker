package com.wppicker.screen.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wppicker.data.PhotoData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    var repository: SearchRepository
) : ViewModel() {

    private val _photos = MutableLiveData<List<PhotoData>>()
    val photos = _photos as LiveData<List<PhotoData>>

    fun loadPhotos(keyword: String, orientation: String?) {
        val mOrientation = if(orientation == "All") null else orientation?.lowercase()

        repository.searchPhoto(keyword, mOrientation, object : OnSearchPhoto {
            override fun onSearch(photoList: List<PhotoData>) {
                _photos.value = photoList
            }
        })

    }
    class Factory @Inject constructor(private val repository: SearchRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(repository) as T
        }
    }
}
