package com.cuervolu.witcherscodex.data.response

/**
 * Clase sellada que representa el resultado de un intento de inicio de sesión.
 */
sealed class LoginResult {
    /**
     * Clase interna que representa un resultado de error durante el inicio de sesión.
     */
    object Error : LoginResult()

    /**
     * Clase interna que representa un resultado exitoso de inicio de sesión.
     *
     * @property verified Indica si la cuenta de usuario ha sido verificada por correo electrónico.
     */
    data class Success(val verified: Boolean) : LoginResult()
}
