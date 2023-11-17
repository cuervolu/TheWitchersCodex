package com.cuervolu.witcherscodex.ui.weapons

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.WeaponService
import com.cuervolu.witcherscodex.domain.models.Weapon
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UpdateWeaponViewModel @Inject constructor(private val weaponService: WeaponService) :
    ViewModel() {
    private val _createWeaponResult = MutableLiveData<Boolean>()
    val createWeaponResult: LiveData<Boolean>
        get() = _createWeaponResult

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog

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
                _showErrorDialog.value = Pair("Error", "Error al leer la entrada del arma")
            }

        )
    }


    fun updateWeapon(
        entryId: String,
        name: String,
        bonuses: String,
        baseDamage: String,
        craftReq: String,
        imageUri: Uri
    ) {
        val currentImageUri = _entryLiveData.value?.imageUrl?.lowercase()
        val newImageUri = imageUri.toString().lowercase()
        val weapon = Weapon(
            entryId = entryId,
            name = name,
            base_damage = baseDamage,
            bonuses = bonuses,
            crafting_req = craftReq,
        )
        if (currentImageUri != newImageUri) {

            Timber.w("Se ha cambiado la imagen")
            weaponService.updateEntry(weapon, imageUri,
                onSuccess = {
                    _createWeaponResult.value = true
                },
                onError = {
                    _createWeaponResult.value = false
                    _showErrorDialog.value =
                        Pair("Error", "Error al actualizar la entrada del arma")
                }
            )
        } else {
            Timber.w("No se ha cambiado la imagen")
            weaponService.updateEntry(weapon,
                onSuccess = {
                    _createWeaponResult.value = true
                },
                onError = {
                    _createWeaponResult.value = false
                    _showErrorDialog.value =
                        Pair("Error", "Error al actualizar la entrada del bestiario")
                }
            )
        }
    }
}