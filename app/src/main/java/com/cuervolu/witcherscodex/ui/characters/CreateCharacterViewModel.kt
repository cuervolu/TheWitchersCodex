package com.cuervolu.witcherscodex.ui.characters

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.CharacterService
import com.cuervolu.witcherscodex.domain.models.BasicInformation
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateCharacterViewModel @Inject constructor(private val characterService: CharacterService) :
    ViewModel() {

    private val _createCharacterResult = MutableLiveData<Boolean>()
    val createCharacterResult: LiveData<Boolean>
        get() = _createCharacterResult

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog

    fun createCharacter(
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

        val character = Character(
            name = name,
            alias = listOf(alias),
            description = description,
            basic_information = basicInformation,

            )

        characterService.createEntry(character, imageUri,
            onSuccess = {
                _createCharacterResult.value = true
            },
            onError = {
                _createCharacterResult.value = false
                _showErrorDialog.value = Pair("Error", "Error al crear la entrada del bestiario")
            }
        )
    }


}
