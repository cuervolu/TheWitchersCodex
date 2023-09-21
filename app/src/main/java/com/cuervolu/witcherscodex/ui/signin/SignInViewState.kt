package com.cuervolu.witcherscodex.ui.signin

/**
 * Clase de estado que representa la vista de registro (sign-in) en la aplicación.
 * Contiene información sobre el estado de los campos de entrada y la carga.
 *
 * @property isLoading Indica si la vista está en un estado de carga.
 * @property isValidEmail Indica si el campo de correo electrónico es válido.
 * @property isValidPassword Indica si el campo de contraseña es válido.
 * @property isValidRealName Indica si el campo de nombre real es válido.
 * @property isValidNickName Indica si el campo de nombre de usuario (nickName) es válido.
 */
data class SignInViewState(
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true,
    val isValidPassword: Boolean = true,
    val isValidRealName: Boolean = true,
    val isValidNickName: Boolean = true
){
    /**
     * Verifica si el usuario ha completado con éxito todos los campos requeridos.
     *
     * @return `true` si todos los campos requeridos son válidos, `false` de lo contrario.
     */
    fun userValidated() = isValidEmail && isValidPassword && isValidRealName && isValidNickName
}
