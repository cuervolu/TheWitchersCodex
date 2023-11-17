package com.cuervolu.witcherscodex.ui.bestiary

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.BestiaryService
import com.cuervolu.witcherscodex.domain.models.Bestiary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateMonsterViewModel @Inject constructor(private val bestiaryService: BestiaryService) :
    ViewModel() {

    private val _createMonsterResult = MutableLiveData<Boolean>()
    val createMonsterResult: LiveData<Boolean>
        get() = _createMonsterResult

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog

    fun createMonster(
        name: String,
        description: String,
        location: String,
        type: String,
        loot: String,
        weakness: String,
        imageUri: Uri
    ) {
        val monster = Bestiary(
            name = name,
            desc = description,
            location = location,
            type = type,
            loot = loot,
            weakness = weakness
        )

        bestiaryService.createEntry(monster, imageUri,
            onSuccess = {
                _createMonsterResult.value = true
            },
            onError = {
                _createMonsterResult.value = false
                _showErrorDialog.value = Pair("Error", "Error al crear la entrada del bestiario")
            }
        )
    }


}
