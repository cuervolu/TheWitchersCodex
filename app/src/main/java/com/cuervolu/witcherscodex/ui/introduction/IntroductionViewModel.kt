package com.cuervolu.witcherscodex.ui.introduction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.core.Event
import com.cuervolu.witcherscodex.data.network.FirebaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para la actividad de introducción.
 *
 * Esta clase ViewModel se encarga de gestionar la lógica y el flujo de datos para la pantalla de introducción de la aplicación.
 * Proporciona funciones para navegar a las pantallas de inicio de sesión y registro y expone LiveData para observar eventos de
 * navegación.
 *
 * @property _navigateToLogin LiveData privado para la navegación a la pantalla de inicio de sesión.
 * @property navigateToLogin LiveData público para observar eventos de navegación a la pantalla de inicio de sesión.
 * @property _navigateToSignIn LiveData privado para la navegación a la pantalla de registro.
 * @property navigateToSignIn LiveData público para observar eventos de navegación a la pantalla de registro.
 */
@HiltViewModel
class IntroductionViewModel @Inject constructor(private val firebaseClient: FirebaseClient) : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToSignIn = MutableLiveData<Event<Boolean>>()
    val navigateToSignIn: LiveData<Event<Boolean>>
        get() = _navigateToSignIn

    private val _navigateToDashboard = MutableLiveData<Event<Boolean>>()
    val navigateToDashboard: LiveData<Event<Boolean>>
        get() = _navigateToDashboard

    init {
        // Verificar si el usuario ya está autenticado con Firebase al inicializar el ViewModel
        val currentUser = firebaseClient.auth.currentUser
        if (currentUser != null) {
            // El usuario ya está autenticado, emitir un evento para navegar al dashboard
            _navigateToDashboard.value = Event(true)
        }
    }

    /**
     * Navega a la pantalla de inicio de sesión.
     */
    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }

    /**
     * Navega a la pantalla de registro.
     */
    fun onSignInSelected() {
        _navigateToSignIn.value = Event(true)
    }
}
