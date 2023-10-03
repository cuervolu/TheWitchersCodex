package com.cuervolu.witcherscodex.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.databinding.ItemCharacterBinding
import com.cuervolu.witcherscodex.utils.GlideImageLoader

class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemCharacterBinding.bind(view)

    fun render(imageModel: String, onClickListener: (String) -> Unit) {
        GlideImageLoader.loadImage(binding.ivCharacter, imageModel)
        itemView.setOnClickListener {
            onClickListener(imageModel)
        }
    }
}