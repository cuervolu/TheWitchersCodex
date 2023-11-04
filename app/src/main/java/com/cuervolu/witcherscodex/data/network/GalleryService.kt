package com.cuervolu.witcherscodex.data.network

import com.google.firebase.storage.StorageReference
import timber.log.Timber

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryService @Inject constructor(
    firebase: FirebaseClient
) {
    private val storage = firebase.storage
    private val storageReference = storage.reference
    private val appCheck = firebase.appCheck


    fun getAllImages(callback: (List<StorageReference>) -> Unit) {
        // Listar todos los elementos del almacenamiento
        storageReference.listAll()
            .addOnSuccessListener { result ->
                // Obtener la lista de referencias de los elementos
                val images = result.items
                callback(images)
            }
            .addOnFailureListener { exception ->
                // Manejar cualquier error que ocurra
                Timber.d("Algo salió mal...")
                callback(emptyList()) // Devuelve una lista vacía en caso de error
            }
    }
}