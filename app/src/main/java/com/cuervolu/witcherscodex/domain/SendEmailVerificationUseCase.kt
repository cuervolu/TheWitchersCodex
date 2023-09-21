package com.cuervolu.witcherscodex.domain

import com.cuervolu.witcherscodex.data.network.AuthenticationService
import javax.inject.Inject

/**
 * Caso de uso para enviar un correo electrónico de verificación al usuario.
 *
 * Este caso de uso se utiliza para enviar un correo electrónico de verificación al usuario registrado
 * utilizando el servicio de autenticación [AuthenticationService].
 *
 * @param authenticationService El servicio de autenticación que se utiliza para enviar el correo electrónico de verificación.
 */
class SendEmailVerificationUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    /**
     * Ejecuta el caso de uso para enviar un correo electrónico de verificación al usuario.
     */
    suspend operator fun invoke() = authenticationService.sendVerificationEmail()
}
