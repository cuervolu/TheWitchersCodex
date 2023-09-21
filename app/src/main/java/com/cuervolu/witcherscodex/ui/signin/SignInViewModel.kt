package com.cuervolu.witcherscodex.ui.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuervolu.witcherscodex.core.Event
import com.cuervolu.witcherscodex.data.network.UserService
import com.cuervolu.witcherscodex.domain.CreateAccountUseCase
import com.cuervolu.witcherscodex.ui.signin.model.UserSignIn
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gestiona la lógica de la pantalla de registro (sign-in) de la aplicación.
 * Permite a los usuarios crear una nueva cuenta ingresando información como nombre real, nombre de usuario, correo electrónico y contraseña.
 *
 * @property createAccountUseCase El caso de uso utilizado para crear una nueva cuenta de usuario.
 * @property userService El servicio utilizado para interactuar con los usuarios
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    val createAccountUseCase: CreateAccountUseCase,
    private val userService: UserService
) :
    ViewModel() {

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyEmail: LiveData<Event<Boolean>>
        get() = _navigateToVerifyEmail

    private val _navigateToDetails = MutableLiveData<Event<Boolean>>()
    val navigateToDetails: LiveData<Event<Boolean>>
        get() = _navigateToDetails

    private val _viewState = MutableStateFlow(SignInViewState())
    val viewState: StateFlow<SignInViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    /**
     * Maneja el evento de registro de un nuevo usuario.
     *
     * @param userSignIn La información de registro proporcionada por el usuario.
     */
    fun onSignInSelected(userSignIn: UserSignIn) {
        val viewState = userSignIn.toSignInViewState()
        if (viewState.userValidated() && userSignIn.isNotEmpty()) {
            signInUser(userSignIn)
        } else {
            onFieldsChanged(userSignIn)
        }
    }

    /**
     * Guarda los datos del usuario en Firestore si no existen previamente.
     *
     * @param user Usuario autenticado en Firebase.
     * @param realname Nombre real del usuario (opcional, por defecto null).
     * @param nickname Nombre de usuario (opcional, por defecto null).
     */
    fun saveUserDataToFirestore(
        user: FirebaseUser?, realname: String? = null,
        nickname: String? = null
    ) {
        userService.saveUserDataToFirestore(user, realname, nickname)
    }


    private fun signInUser(userSignIn: UserSignIn) {
        viewModelScope.launch {
            _viewState.value = SignInViewState(isLoading = true)
            val accountCreated = createAccountUseCase(userSignIn)
            if (accountCreated) {
                _navigateToVerifyEmail.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }
            _viewState.value = SignInViewState(isLoading = false)
        }
    }

    /**
     * Actualiza el estado de la vista en función de los cambios en los campos de entrada.
     *
     * @param userSignIn La información de registro proporcionada por el usuario.
     */
    fun onFieldsChanged(userSignIn: UserSignIn) {
        _viewState.value = userSignIn.toSignInViewState()
    }

    /**
     * Maneja el evento de inicio de sesión seleccionado por el usuario.
     */
    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidOrEmptyPassword(password: String, passwordConfirmation: String): Boolean =
        (password.length >= MIN_PASSWORD_LENGTH && password == passwordConfirmation) || password.isEmpty() || passwordConfirmation.isEmpty()

    private fun isValidName(name: String): Boolean =
        name.length >= MIN_PASSWORD_LENGTH || name.isEmpty()

    private fun UserSignIn.toSignInViewState(): SignInViewState {
        return SignInViewState(
            isValidEmail = isValidOrEmptyEmail(email!!),
            isValidPassword = isValidOrEmptyPassword(password, passwordConfirmation),
            isValidNickName = isValidName(nickname!!),
            isValidRealName = isValidName(realname!!)
        )
    }
}