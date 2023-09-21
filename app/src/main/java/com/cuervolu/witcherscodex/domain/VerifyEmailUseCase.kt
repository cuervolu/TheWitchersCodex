package com.cuervolu.witcherscodex.domain

import com.cuervolu.witcherscodex.data.network.AuthenticationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para verificar si la cuenta de correo electrónico del usuario ha sido verificada.
 *
 * Este caso de uso se utiliza para verificar si la cuenta de correo electrónico del usuario ha sido
 * verificada utilizando el servicio de autenticación [AuthenticationService].
 *
 * @param authenticationService El servicio de autenticación que se utiliza para verificar el estado de la cuenta de correo electrónico.
 */
class VerifyEmailUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    /**
     * Ejecuta el caso de uso para verificar si la cuenta de correo electrónico del usuario ha sido verificada.
     *
     * @return Un [Flow] que emite un valor booleano que indica si la cuenta de correo electrónico del usuario ha sido verificada.
     */
    operator fun invoke(): Flow<Boolean> = authenticationService.verifiedAccount

}