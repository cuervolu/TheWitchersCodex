package com.cuervolu.witcherscodex.ui.signin.model

import com.cuervolu.witcherscodex.ui.login.common.UserData

/**
 * Clase de datos que representa los datos necesarios para el proceso de registro (sign in) de un usuario.
 *
 * @property realName El nombre real del usuario.
 * @property nickName El nombre de usuario (nick) elegido por el usuario.
 * @property email La dirección de correo electrónico del usuario.
 * @property password La contraseña elegida por el usuario.
 * @property passwordConfirmation La confirmación de la contraseña elegida por el usuario.
 */
data class UserSignIn(
    override val email: String? = "",
    override val nickname: String? = "",
    override val realname: String? = "",
    override val imageUrl: String? = null,
    val password: String,
    val passwordConfirmation: String
) : UserData {
    /**
     * Verifica si todos los campos de registro no están vacíos.
     *
     * @return `true` si todos los campos no están vacíos; `false` de lo contrario.
     */
    fun isNotEmpty() =
        realname!!.isNotEmpty() && nickname!!.isNotEmpty() && email!!.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty()
}
