package com.cuervolu.witcherscodex.ui.weapons

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.WeaponService
import com.cuervolu.witcherscodex.domain.models.Weapon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateWeaponViewModel @Inject constructor(private val weaponService: WeaponService) :
    ViewModel() {
    private val _createWeaponResult = MutableLiveData<Boolean>()
    val createWeaponResult: LiveData<Boolean>
        get() = _createWeaponResult

    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog

    fun createWeapon(
        name: String,
        bonuses: String,
        baseDamage: String,
        crafting: String,
        imageUri: Uri
    ) {
        val weapon = Weapon(
            name = name,
            base_damage = baseDamage,
            bonuses = bonuses,
            crafting_req = crafting

        )

        weaponService.createEntry(weapon, imageUri,
            onSuccess = {
                _createWeaponResult.value = true
            },
            onError = {
                _createWeaponResult.value = false
                _showErrorDialog.value = Pair("Error", "Error al crear la entrada del armas")
            }
        )
    }
}