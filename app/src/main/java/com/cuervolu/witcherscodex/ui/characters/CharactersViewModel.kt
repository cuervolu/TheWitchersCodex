package com.cuervolu.witcherscodex.ui.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.CharacterService
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(private val characterService: CharacterService) :
    ViewModel() {

    private val _characterLiveData = MutableLiveData<List<Character>>()
    val characterLiveData: LiveData<List<Character>> get() = _characterLiveData
    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun loadCharacters() {
        CoroutineScope(Dispatchers.IO).launch {
            characterService.getAllCharacters(
                onSuccess = { characters ->
                    // Actualiza _featuredArticles con los artículos obtenidos
                    _characterLiveData.postValue(characters)
                    _isLoading.postValue(false)

                },
                onError = {
                    _showErrorDialog.value = true
                    _isLoading.postValue(false)
                }
            )
        }
    }
}
