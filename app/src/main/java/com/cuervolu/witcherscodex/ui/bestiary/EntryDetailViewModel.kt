package com.cuervolu.witcherscodex.ui.bestiary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.BestiaryService
import com.cuervolu.witcherscodex.domain.models.Bestiary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EntryDetailViewModel @Inject constructor(private val bestiaryService: BestiaryService) :
    ViewModel() {


    private val _deleteMonsterResult = MutableLiveData<Boolean>()
    val deleteMonsterResult: LiveData<Boolean>
        get() = _deleteMonsterResult

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

    fun deleteEntry(entryId: String) {
        bestiaryService.deleteEntry(
            entryId,
            onSuccess = {
                _deleteMonsterResult.value = true
            },
            onError = {
                _deleteMonsterResult.value = false
                _showErrorDialog.value = Pair("Error", "Error al eliminar la entrada del bestiario")
                throw Exception("Error al eliminar la entrada del bestiario")

            }
        )
    }

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog
}

