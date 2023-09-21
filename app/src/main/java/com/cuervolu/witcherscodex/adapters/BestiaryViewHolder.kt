package com.cuervolu.witcherscodex.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.databinding.ItemCharacterBinding
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.cuervolu.witcherscodex.utils.GlideImageLoader

class BestiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemCharacterBinding.bind(view)

    fun render(bestiaryModel: Bestiary, onClickListener: (Bestiary) -> Unit) {
        binding.txtCharacterName.text = bestiaryModel.name
        binding.txtCharacterDesc.text = bestiaryModel.desc
        binding.txtCharacterProfession.text = bestiaryModel.type
        GlideImageLoader.loadImage(binding.ivCharacter, bestiaryModel.image)
        itemView.setOnClickListener {
            onClickListener(bestiaryModel)
        }
    }
}