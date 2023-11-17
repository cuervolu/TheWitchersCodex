package com.cuervolu.witcherscodex.ui.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.CharacterService
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(private val characterService: CharacterService) :
    ViewModel() {


    private val _deleteCharacterResult = MutableLiveData<Boolean>()
    val deleteCharacterResult: LiveData<Boolean>
        get() = _deleteCharacterResult

    private val _entryLiveData = MutableLiveData<Character>()
    val entryLiveData: LiveData<Character>
        get() = _entryLiveData

    fun loadEntry(entryId: String) {
        characterService.readEntry(
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
        characterService.deleteEntry(
            entryId,
            onSuccess = {
                _deleteCharacterResult.value = true
            },
            onError = {
                _deleteCharacterResult.value = false
                _showErrorDialog.value = Pair("Error", "Error al eliminar la entrada del personaje")
                throw Exception("Error al eliminar la entrada del personaje")

            }
        )
    }

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog
}