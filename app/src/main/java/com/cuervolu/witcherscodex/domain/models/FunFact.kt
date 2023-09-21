package com.cuervolu.witcherscodex.domain.models

import java.util.Date

data class FunFact(
    val title: String, // Título del FunFact
    val description: String, // Descripción del FunFact
    val imageUrl: String, // URL de la imagen asociada al FunFact
    val category: String, // Categoría o etiqueta del FunFact
    val date: Date // Fecha de publicación o actualización del FunFact
)
