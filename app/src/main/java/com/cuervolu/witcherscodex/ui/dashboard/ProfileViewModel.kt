package com.cuervolu.witcherscodex.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuervolu.witcherscodex.core.Event
import com.cuervolu.witcherscodex.data.network.AuthenticationService
import com.cuervolu.witcherscodex.data.network.UserService
import com.cuervolu.witcherscodex.domain.models.User
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gestiona la lógica relacionada con el perfil del usuario en la aplicación.
 *
 * Este ViewModel se encarga de obtener y actualizar los datos del usuario, mostrar diálogos
 * de confirmación y errores, y manejar la acción de cerrar sesión.
 *
 * @param userService Servicio para obtener los datos del usuario.
 * @param authenticationService Servicio para gestionar la autenticación y el cierre de sesión.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userService: UserService,
    private val authenticationService: AuthenticationService
) : ViewModel() {
    // LiveData para los datos del usuario
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    // LiveData para mostrar el diálogo de confirmación de cierre de sesión
    private val _showLogoutConfirmation = MutableLiveData<Boolean>()
    val showLogoutConfirmation: LiveData<Boolean> = _showLogoutConfirmation

    // LiveData para navegar al perfil
    private val _navigateToProfile = MutableLiveData<Boolean>()
    val navigateToProfile: LiveData<Boolean> = _navigateToProfile

    // LiveData para navegar a la pantalla de inicio de sesión con eventos
    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    // LiveData para mostrar un diálogo de error con eventos
    private val _showErrorDialog = MutableLiveData<Pair<String, String>>()
    val showErrorDialog: LiveData<Pair<String, String>>
        get() = _showErrorDialog

    /**
     * Obtiene los datos del usuario y los actualiza en [_userData].
     */
    fun getUserData() {
        viewModelScope.launch {
            val userSignIn = userService.getUserDataFromFirestore()
            _userData.value = userSignIn
        }
    }

    /**
     * Indica que se ha seleccionado el perfil.
     */
    fun onProfileSelected() {
        _navigateToProfile.value = true
    }

    /**
     * Muestra u oculta el diálogo de confirmación de cierre de sesión.
     *
     * @param value `true` para mostrar el diálogo, `false` para ocultarlo.
     */
    fun onLogoutConfirmationDialogShown(value: Boolean) {
        _showLogoutConfirmation.value = value
    }

    /**
     * Realiza la acción de cerrar sesión.
     * Maneja el cierre de sesión y la navegación a la pantalla de inicio de sesión.
     */
    suspend fun onLogOutSelected() {
        try {
            val logout = authenticationService.logOut()
            if (logout) {
                onProfileNavigated()
                _navigateToLogin.value = Event(true)
            } else {
                _showErrorDialog.value = Pair("No se ha podido cerrar sesión", "Inténtalo más tarde")
            }
        } catch (e: FirebaseAuthException) {
            _showErrorDialog.value = Pair("No se ha podido cerrar sesión", "${e.localizedMessage}")
        }
    }

    /**
     * Indica que se ha navegado al perfil.
     */
    fun onProfileNavigated() {
        _navigateToProfile.value = false
    }
}
