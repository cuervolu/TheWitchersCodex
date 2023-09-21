package com.cuervolu.witcherscodex.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {
    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToGallery: LiveData<Boolean> = _navigateToGallery

    fun onGallerySelected() {
        _navigateToGallery.value = true
    }

    fun onGalleryNavigated() {
        _navigateToGallery.value = false
    }

}