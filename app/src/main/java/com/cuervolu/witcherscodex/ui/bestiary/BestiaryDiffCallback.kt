package com.cuervolu.witcherscodex.ui.bestiary

import androidx.recyclerview.widget.DiffUtil
import com.cuervolu.witcherscodex.domain.models.Bestiary

class BestiaryDiffCallback : DiffUtil.ItemCallback<Bestiary>() {
    override fun areItemsTheSame(oldItem: Bestiary, newItem: Bestiary): Boolean {
        // Implementa la lógica para determinar si los elementos son los mismos.
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Bestiary, newItem: Bestiary): Boolean {
        // Implementa la lógica para determinar si los contenidos son iguales.
        return oldItem == newItem // Esto compara todo el objeto, ajusta según tu modelo.
    }
}
