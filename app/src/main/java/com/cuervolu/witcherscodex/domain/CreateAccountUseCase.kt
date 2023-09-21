package com.cuervolu.witcherscodex.domain

import com.cuervolu.witcherscodex.data.network.AuthenticationService
import com.cuervolu.witcherscodex.data.network.FirebaseClient
import com.cuervolu.witcherscodex.data.network.UserService
import com.cuervolu.witcherscodex.ui.signin.model.UserSignIn
import timber.log.Timber
import javax.inject.Inject

/**
 * Caso de uso para la creación de una cuenta de usuario.
 *
 * Este caso de uso combina las operaciones de crear una cuenta de usuario a través del servicio de autenticación
 * [AuthenticationService] y la creación de una entrada de usuario en la base de datos [UserService].
 *
 * @param authenticationService El servicio de autenticación que se utiliza para crear una cuenta de usuario.
 * @param firebaseClient El cliente de firebase que se utiliza para obtener su instancia.
 * @param userService El servicio que gestiona la creación de entradas de usuario en la base de datos.
 */
class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val firebaseClient: FirebaseClient,
    private val userService: UserService
) {

    /**
     * Ejecuta el caso de uso para crear una cuenta de usuario.
     *
     * @param userSignIn Los datos de inicio de sesión y registro del usuario.
     * @return `true` si la cuenta se crea con éxito y la entrada del usuario se registra en la base de datos, `false` en caso contrario.
     */
    suspend operator fun invoke(userSignIn: UserSignIn): Boolean {
        try {
            val accountCreated =
                authenticationService.createAccount(userSignIn.email!!, userSignIn.password)
            return if (accountCreated != null) {
                val user = firebaseClient.auth.currentUser
                if (user != null) {
                    userService.saveUserDataToFirestore(
                        user,
                        userSignIn.realname,
                        userSignIn.nickname
                    )
                    true
                } else {
                    // El usuario no está autenticado después de crear la cuenta
                    false
                }
            } else {
                // La creación de la cuenta falló
                false
            }
        } catch (e: Exception) {
            Timber.e("${e.localizedMessage}")
            return false
        }
    }
}
