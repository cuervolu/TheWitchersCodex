package com.cuervolu.witcherscodex.ui.login
/**
 * Clase de estado que representa el estado de la pantalla de inicio de sesión.
 *
 * @property isLoading Indica si la pantalla de inicio de sesión está en un estado de carga.
 * @property isValidEmail Indica si el campo de correo electrónico es válido.
 * @property isValidPassword Indica si el campo de contraseña es válido.
 */
data class LoginViewState(
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true,
    val isValidPassword: Boolean = true
)