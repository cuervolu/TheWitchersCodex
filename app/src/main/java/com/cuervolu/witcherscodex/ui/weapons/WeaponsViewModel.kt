package com.cuervolu.witcherscodex.ui.weapons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.data.network.WeaponService
import com.cuervolu.witcherscodex.domain.models.Character
import com.cuervolu.witcherscodex.domain.models.Weapon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeaponsViewModel @Inject constructor(private val weaponService: WeaponService) : ViewModel() {
    private val _weaponLiveData = MutableLiveData<List<Weapon>>()
    val weaponLiveData: LiveData<List<Weapon>> get() = _weaponLiveData
    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun loadWeapons() {
        CoroutineScope(Dispatchers.IO).launch {
            weaponService.getAllWeapons(
                onSuccess = { weapons ->
                    _weaponLiveData.postValue(weapons)
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