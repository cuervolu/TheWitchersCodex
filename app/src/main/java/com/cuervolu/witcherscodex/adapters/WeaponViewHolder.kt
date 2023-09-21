package com.cuervolu.witcherscodex.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.databinding.ItemCharacterBinding
import com.cuervolu.witcherscodex.domain.models.Weapon
import com.cuervolu.witcherscodex.utils.GlideImageLoader

class WeaponViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
    val binding = ItemCharacterBinding.bind(view)

    fun render(weaponModel: Weapon, onClickListener: (Weapon) -> Unit) {
        binding.txtCharacterName.text = weaponModel.name
        binding.txtCharacterDesc.text = weaponModel.crafting_req
        binding.txtCharacterProfession.text = weaponModel.base_damage
        GlideImageLoader.loadImage(binding.ivCharacter, weaponModel.imageUrl)
        itemView.setOnClickListener {
            onClickListener(weaponModel)
        }
    }
}