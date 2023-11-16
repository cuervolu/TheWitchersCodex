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

    fun createMonster(name: String, description: String, location: String, type: String, imageUri: Uri) {
        val monster = Bestiary(name = name, desc = description, location = location, type = type)

        bestiaryService.createEntry(monster, imageUri,
            onSuccess = {
                _createMonsterResult.value = true
            },
            onError = {
                _createMonsterResult.value = false
            }
        )
    }
}
