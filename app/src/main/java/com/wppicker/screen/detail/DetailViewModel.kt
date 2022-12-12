package com.wppicker.screen.detail

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wppicker.data.PhotoData

class DetailViewModel(private val repository: DetailRepository) : ViewModel() {
    private val _intent = MutableLiveData<Intent>()
    val intent = _intent as LiveData<Intent>

    private val _photo = MutableLiveData<PhotoData>()
    val photo = _photo as LiveData<PhotoData>

    fun loadPhoto(photoIdx: String) {
        repository.loadPhoto(photoIdx, object : OnPhotoLoaded {
            override fun onPhotoLoaded(photo: PhotoData) {
                _photo.value = photo
            }
        })
    }

    fun setPortfolio(portfolioURL: String) {
        val mIntent = Intent(Intent.ACTION_VIEW)
        mIntent.data = Uri.parse(portfolioURL)
        _intent.value = mIntent
    }

    class Factory(private val repository: DetailRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(repository) as T
        }
    }
}