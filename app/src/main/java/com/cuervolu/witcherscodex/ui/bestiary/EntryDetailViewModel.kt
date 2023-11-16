package com.cuervolu.witcherscodex.ui.bestiary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.BestiaryService
import com.cuervolu.witcherscodex.domain.models.Bestiary
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EntryDetailViewModel @Inject constructor(private val bestiaryService: BestiaryService) :
    ViewModel() {

    private val _entryLiveData = MutableLiveData<Bestiary>()
    val entryLiveData: LiveData<Bestiary>
        get() = _entryLiveData

    fun loadEntry(entryId: String) {
        bestiaryService.readEntry(
            entryId,
            onSuccess = { entry ->
                _entryLiveData.value = entry
            },
            onError = {
                throw Exception("Error al leer la entrada del bestiario")
            }
        )
    }
}

