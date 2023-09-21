package com.cuervolu.witcherscodex.data.network


import android.content.Context
import com.cuervolu.witcherscodex.data.response.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
/**
 * Clase que proporciona funcionalidades relacionadas con la autenticación de usuarios a través
 * de Firebase.
 *
 * @property firebase Cliente Firebase que se utiliza para interactuar con la autenticación.
 * @property verifiedAccount Un flujo de [Boolean] que emite un valor cada segundo, indicando si
 * la cuenta de correo electrónico ha sido verificada.
 */
class AuthenticationService @Inject constructor(
    private val firebase: FirebaseClient,
    private val googleSignInClient: GoogleClient,
    val context: Context
) {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest

    /**
     * Un flujo que verifica si la cuenta de correo electrónico ha sido verificada y emite un
     * [Boolean] correspondiente.
     */
    val verifiedAccount: Flow<Boolean> = flow {
        while (true) {
            val verified = verifyEmailIsVerified()
            emit(verified)
            delay(1000)
        }
    }

    /**
     * Realiza un intento de inicio de sesión con el correo electrónico y la contraseña
     * proporcionados.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     *
     * @return El resultado del inicio de sesión encapsulado en [LoginResult].
     */
    suspend fun login(email: String, password: String): LoginResult = runCatching {
        firebase.auth.signInWithEmailAndPassword(email, password).await()
    }.toLoginResult()

    /**
     * Crea una nueva cuenta de usuario con el correo electrónico y la contraseña proporcionados.
     *
     * @param email Correo electrónico del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     *
     * @return El resultado de la creación de la cuenta en forma de [AuthResult] o `null` si
     * la operación falla.
     */
    suspend fun createAccount(email: String, password: String): AuthResult? {
        return firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun logOut(): Boolean {
        val currentUser: FirebaseUser? = firebase.auth.currentUser

        if (currentUser != null) {
            // Verificamos si se autenticó con Google
            val isGoogleSignIn = checkIfGoogleSignIn(currentUser)

            if (isGoogleSignIn) {
                try {
                    // Cierra la sesión de Firebase
                    firebase.auth.signOut()

                    // Cierra la sesión de Google usando la instancia inyectada
                    googleSignInClient.client.signOut().await()

                    // La sesión de Firebase y Google se cerró con éxito
                    return true
                } catch (e: Exception) {
                    Timber.e(e.localizedMessage)
                    e.printStackTrace()
                    return false
                }
            } else {
                try {
                    // Si no se autenticó con Google, solo cerramos la sesión de Firebase
                    firebase.auth.signOut()

                    // La sesión de Firebase se cerró con éxito
                    return true
                } catch (e: Exception) {
                    Timber.e(e.localizedMessage)
                    e.printStackTrace()
                    return false
                }
            }
        }
        // No había usuario autenticado, no se realizó ninguna operación
        return false
    }

    suspend fun checkUserExistsInFirestore(): Boolean {
        val currentUser = firebase.auth.currentUser
        if (currentUser != null) {
            val userDocument = firebase.db.collection("users").document(currentUser.uid)
            return try {
                userDocument.get().await().exists()
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    private fun checkIfGoogleSignIn(user: FirebaseUser): Boolean {
        val providers = user.providerData
        for (profile in providers) {
            if (GoogleAuthProvider.PROVIDER_ID == profile.providerId) {
                return true
            }
        }
        return false
    }

    /**
     * Envía un correo electrónico de verificación al usuario actualmente autenticado.
     *
     * @return `true` si el correo electrónico de verificación se envió correctamente, `false`
     * en caso contrario.
     */
    suspend fun sendVerificationEmail() = runCatching {
        firebase.auth.currentUser?.sendEmailVerification()?.await() ?: false
    }.isSuccess

    fun isUserAuthenticated(): Boolean {
        val currentUser = firebase.auth.currentUser
        return currentUser != null
    }

    private suspend fun verifyEmailIsVerified(): Boolean {
        firebase.auth.currentUser?.reload()?.await()
        return firebase.auth.currentUser?.isEmailVerified ?: false
    }

    private fun Result<AuthResult>.toLoginResult() = when (val result = getOrNull()) {
        null -> LoginResult.Error
        else -> {
            val userId = result.user
            checkNotNull(userId)
            LoginResult.Success(result.user?.isEmailVerified ?: false)
        }
    }
}
