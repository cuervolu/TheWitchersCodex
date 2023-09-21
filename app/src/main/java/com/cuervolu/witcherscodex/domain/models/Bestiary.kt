package com.cuervolu.witcherscodex.domain.models

data class Bestiary(
    val name: String = "", // Nombre del ser
    val type: String = "", // Tipo del ser (por ejemplo, "Relicts")
    val location: String = "", // Ubicación donde se encuentra el ser
    val desc: String = "", // Descripción del ser
    val image: String = "", // URL de la imagen asociada al ser
    val loot: String? = "", // Botín que se puede obtener del ser (puede ser nulo)
    val weakness: String? = "" // Debilidad del ser (puede ser nulo)
)
