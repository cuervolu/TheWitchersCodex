package com.cuervolu.witcherscodex.ui.weapons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.WeaponService
import com.cuervolu.witcherscodex.domain.models.Weapon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeaponDetailViewModel @Inject constructor(private val weaponService: WeaponService) :
    ViewModel() {


    private val _deleteWeaponResult = MutableLiveData<Boolean>()
    val deleteWeaponResult: LiveData<Boolean>
        get() = _deleteWeaponResult

    private val _entryLiveData = MutableLiveData<Weapon>()
    val entryLiveData: LiveData<Weapon>
        get() = _entryLiveData

    fun loadEntry(entryId: String) {
        weaponService.readEntry(
            entryId,
            onSuccess = { entry ->
                _entryLiveData.value = entry
            },
            onError = {
                throw Exception("Error al leer la entrada de armas")
            }
        )
    }

    fun deleteEntry(entryId: String) {
        weaponService.deleteEntry(
            entryId,
            onSuccess = {
                _deleteWeaponResult.value = true
            },
            onError = {
                _deleteWeaponResult.value = false
                _showErrorDialog.value = Pair("Error", "Error al eliminar la entrada del arma")
                throw Exception("Error al eliminar la entrada del arma")

            }
        )
    }

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog
}
