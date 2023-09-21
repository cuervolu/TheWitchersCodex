package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.cuervolu.witcherscodex.utils.GlideImageLoader

class BestiaryFeaturedAdapter(var bestiary: List<Bestiary>) :
    RecyclerView.Adapter<BestiaryFeaturedAdapter.BestiaryViewHolder>() {

    inner class BestiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val beastImageView: ImageView = itemView.findViewById(R.id.beastImageView)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestiaryViewHolder {
        // Infla el diseño del elemento de lista y crea una instancia de BestiaryViewHolder
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.bestiary_card_design, parent, false)
        return BestiaryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BestiaryViewHolder, position: Int) {
        // Configura las vistas dentro del elemento con los datos del bestiario correspondiente
        val entry = bestiary[position]
        holder.nameTextView.text = entry.name
        // Cargar la imagen utilizando Glide
        GlideImageLoader.loadImage(holder.beastImageView, entry.image)

    }


    override fun getItemCount(): Int {
        // Devuelve el número total de elementos en la lista
        return bestiary.size
    }

}