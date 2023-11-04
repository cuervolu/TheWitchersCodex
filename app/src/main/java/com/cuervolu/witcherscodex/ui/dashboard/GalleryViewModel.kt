package com.cuervolu.witcherscodex.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.GalleryService

import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(private val galleryService: GalleryService) :
    ViewModel() {
    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToGallery: LiveData<Boolean> = _navigateToGallery


    private val _galleryLiveData = MutableLiveData<List<String>>()
    val galleryLiveData: LiveData<List<String>> get() = _galleryLiveData
    private var _showErrorDialog = MutableLiveData(false)

    fun onGallerySelected() {
        _navigateToGallery.value = true
    }

    fun onGalleryNavigated() {
        _navigateToGallery.value = false
    }


    fun loadImages() {
        galleryService.getAllImages { images ->
            // Aquí puedes trabajar con la lista de referencias de imágenes
            for (imageRef in images) {
                Timber.d("$images")
                // Puedes obtener la URL de cada imagen
                val imageUrl = imageRef.downloadUrl.toString()
                _galleryLiveData.postValue(listOf(imageUrl))
            }
        }
    }

}