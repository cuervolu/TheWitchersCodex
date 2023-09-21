package com.cuervolu.witcherscodex.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cuervolu.witcherscodex.databinding.ItemCharacterBinding
import com.cuervolu.witcherscodex.domain.models.Character
import com.cuervolu.witcherscodex.utils.GlideImageLoader
import timber.log.Timber

class CharacterViewHolder(view: View) : ViewHolder(view) {

    val binding = ItemCharacterBinding.bind(view)

    fun render(characterModel: Character, onClickListener: (Character) -> Unit) {
        binding.txtCharacterName.text = characterModel.name
        binding.txtCharacterDesc.text = characterModel.description
        binding.txtCharacterProfession.text = characterModel.personal_information?.profession
        GlideImageLoader.loadImage(binding.ivCharacter, characterModel.image_url)
        itemView.setOnClickListener {
            onClickListener(characterModel)
        }
    }
}