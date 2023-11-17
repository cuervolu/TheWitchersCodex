package com.cuervolu.witcherscodex.ui.bestiary

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.BestiaryService
import com.cuervolu.witcherscodex.domain.models.Bestiary
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UpdateEntryViewModel @Inject constructor(private val bestiaryService: BestiaryService) :
    ViewModel() {

    private val _createMonsterResult = MutableLiveData<Boolean>()
    val createMonsterResult: LiveData<Boolean>
        get() = _createMonsterResult

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog

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
                _showErrorDialog.value = Pair("Error", "Error al leer la entrada del bestiario")
            }

        )
    }


    fun updateMonster(
        entryId: String,
        name: String,
        description: String,
        location: String,
        type: String,
        loot: String,
        weakness: String,
        imageUri: Uri
    ) {
        val currentImageUri = _entryLiveData.value?.image?.lowercase()
        val newImageUri = imageUri.toString().lowercase()
        val monster = Bestiary(
            entryId = entryId,
            name = name,
            desc = description,
            location = location,
            type = type,
            loot = loot,
            weakness = weakness
        )
        if (currentImageUri != newImageUri) {

            Timber.w("Se ha cambiado la imagen")
            bestiaryService.updateEntry(monster, imageUri,
                onSuccess = {
                    _createMonsterResult.value = true
                },
                onError = {
                    _createMonsterResult.value = false
                    _showErrorDialog.value =
                        Pair("Error", "Error al actualizar la entrada del bestiario")
                }
            )
        } else {
            Timber.w("No se ha cambiado la imagen")
            bestiaryService.updateEntry(monster,
                onSuccess = {
                    _createMonsterResult.value = true
                },
                onError = {
                    _createMonsterResult.value = false
                    _showErrorDialog.value =
                        Pair("Error", "Error al actualizar la entrada del bestiario")
                }
            )
        }
    }
}