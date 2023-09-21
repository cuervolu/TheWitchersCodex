package com.cuervolu.witcherscodex.ui.login.model
/**
 * Clase de datos que representa la información de inicio de sesión del usuario.
 *
 * Esta clase se utiliza para encapsular la información necesaria para iniciar sesión en la aplicación, incluyendo el correo electrónico,
 * la contraseña y una bandera que indica si se debe mostrar un cuadro de diálogo de error en caso de problemas durante el inicio de sesión.
 *
 * @property email El correo electrónico del usuario.
 * @property password La contraseña del usuario.
 * @property showErrorDialog Indica si se debe mostrar un cuadro de diálogo de error en caso de problemas durante el inicio de sesión.
 */
data class UserLogin(
    val email: String = "",
    val password: String = "",
    val showErrorDialog: Boolean = false
)
