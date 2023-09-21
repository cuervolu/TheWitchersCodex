package com.cuervolu.witcherscodex.ui.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuervolu.witcherscodex.core.Event
import com.cuervolu.witcherscodex.domain.SendEmailVerificationUseCase
import com.cuervolu.witcherscodex.domain.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel utilizado para gestionar la lógica de la actividad de verificación de cuenta.
 *
 * Esta clase se encarga de iniciar el proceso de verificación de cuenta y manejar las acciones
 * relacionadas con la verificación, como mostrar el botón de continuar una vez que la verificación
 * se ha completado con éxito.
 *
 * @property sendEmailVerificationUseCase El caso de uso para enviar un correo de verificación.
 * @property verifyEmailUseCase El caso de uso para verificar el correo electrónico.
 * @property _navigateToVerifyAccount Un [MutableLiveData] que representa la navegación a la pantalla de verificación de cuenta.
 * @property navigateToVerifyAccount Un [LiveData] que permite observar la navegación a la pantalla de verificación de cuenta.
 * @property _showContinueButton Un [MutableLiveData] que indica si se debe mostrar el botón de continuar.
 * @property showContinueButton Un [LiveData] que permite observar si se debe mostrar el botón de continuar.
 */
@HiltViewModel
class VerificationViewModel @Inject constructor(
    val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _navigateToVerifyAccount = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyAccount: LiveData<Event<Boolean>>
        get() = _navigateToVerifyAccount

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    init {
        viewModelScope.launch { sendEmailVerificationUseCase() }
        viewModelScope.launch {

            verifyEmailUseCase()
                .catch {
                    Timber.i("Verification error: ${it.message}")
                }
                .collect { verification ->
                    if(verification){
                        _showContinueButton.value = Event(verification)
                    }
                }

        }
    }
    /**
     * Método llamado cuando se selecciona el botón para ir a la pantalla de verificación de cuenta.
     */
    fun onGoToDetailSelected() {
        _navigateToVerifyAccount.value = Event(true)
    }
}