package com.cuervolu.witcherscodex.ui.characters

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.CharacterService
import com.cuervolu.witcherscodex.domain.models.BasicInformation
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UpdateCharacterViewModel @Inject constructor(private val characterService: CharacterService) :
    ViewModel() {
    private val _createCharacterResult = MutableLiveData<Boolean>()
    val createCharacterResult: LiveData<Boolean>
        get() = _createCharacterResult

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog

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

    fun updateCharacter(
        entryId: String,
        name: String,
        description: String,
        alias: String,
        hairColor: String,
        eyeColor: String,
        race: String,
        skinColor: String,
        gender: String,
        nationality: String,
        born: String,
        imageUri: Uri
    ) {
        val basicInformation = BasicInformation(
            hairColor,
            eyeColor,
            skinColor,
            race,
            gender,
            "1.75",
            nationality,
            born,
        )


        val currentImageUri = _entryLiveData.value?.image_url?.lowercase()
        val newImageUri = imageUri.toString().lowercase()
        val character = Character(
            entryId = entryId,
            name = name,
            alias = listOf(alias),
            description = description,
            basic_information = basicInformation,
        )
        if (currentImageUri != newImageUri) {

            Timber.w("Se ha cambiado la imagen")
            characterService.updateEntry(character, imageUri,
                onSuccess = {
                    _createCharacterResult.value = true
                },
                onError = {
                    _createCharacterResult.value = false
                    _showErrorDialog.value =
                        Pair("Error", "Error al actualizar la entrada del personaje")
                }
            )
        } else {
            Timber.w("No se ha cambiado la imagen")
            characterService.updateEntry(character,
                onSuccess = {
                    _createCharacterResult.value = true
                },
                onError = {
                    _createCharacterResult.value = false
                    _showErrorDialog.value =
                        Pair("Error", "Error al actualizar la entrada del bestiario")
                }
            )
        }
    }
}