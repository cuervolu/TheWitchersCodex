package com.cuervolu.witcherscodex.domain.models

data class User(
    var email: String?,
    var realname: String?,
    var nickname: String?,
    var imageUrl: String?
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this(null, null, null, null)

    // Función para validar y limpiar los valores antes de guardar en Firestore
    fun cleanValues() {
        email = email?.takeIf { it.isNotBlank() }
        realname = realname?.takeIf { it.isNotBlank() }
        nickname = nickname?.takeIf { it.isNotBlank() }
        imageUrl = imageUrl?.takeIf { it.isNotBlank() }
    }
}
