package com.cuervolu.witcherscodex.data.network

interface CRUDService<T> {
    fun createEntry(entry: T, onSuccess: () -> Unit, onError: () -> Unit)
    fun readEntry(entryId: String, onSuccess: (T) -> Unit, onError: () -> Unit)
    fun updateEntry(entry: T, onSuccess: () -> Unit, onError: () -> Unit)
    fun deleteEntry(entryId: String, onSuccess: () -> Unit, onError: () -> Unit)
}