package com.cuervolu.witcherscodex.data.network

import android.net.Uri

interface CRUDService<T> {
    fun createEntry(entry: T, imageUri: Uri?, onSuccess: () -> Unit, onError: () -> Unit)
    fun readEntry(entryId: String, onSuccess: (T) -> Unit, onError: () -> Unit)
    fun updateEntry(entry: T, imageUri: Uri? = null, onSuccess: () -> Unit, onError: () -> Unit)
    fun deleteEntry(entryId: String, onSuccess: () -> Unit, onError: () -> Unit)
}