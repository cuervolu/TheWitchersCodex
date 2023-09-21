package com.cuervolu.witcherscodex.domain

import com.cuervolu.witcherscodex.data.network.AuthenticationService
import com.cuervolu.witcherscodex.data.response.LoginResult
import javax.inject.Inject

/**
 * Caso de uso para el inicio de sesión de usuario.
 *
 * Este caso de uso se utiliza para iniciar sesión de un usuario utilizando su dirección de correo electrónico
 * y contraseña a través del servicio de autenticación [AuthenticationService].
 *
 * @param authenticationService El servicio de autenticación que se utiliza para el inicio de sesión de usuario.
 */
class LoginUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    /**
     * Ejecuta el caso de uso para iniciar sesión de usuario.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @return Un objeto [LoginResult] que representa el resultado del inicio de sesión.
     */
    suspend operator fun invoke(email: String, password: String): LoginResult =
        authenticationService.login(email, password)
}
