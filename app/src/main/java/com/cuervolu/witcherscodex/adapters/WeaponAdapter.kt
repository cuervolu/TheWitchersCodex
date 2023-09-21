package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.domain.models.Character
import com.cuervolu.witcherscodex.domain.models.Weapon
import com.cuervolu.witcherscodex.ui.characters.CharactersDiffUtil
import com.cuervolu.witcherscodex.ui.weapons.WeaponsDiffUtil

class WeaponAdapter(
    var weaponsList: List<Weapon>,
    private val onClickListener: (Weapon) -> Unit
) : RecyclerView.Adapter<WeaponViewHolder>() {

    fun updateList(newList: List<Weapon>) {
        val weaponDiff = WeaponsDiffUtil(weaponsList, newList)
        val result = DiffUtil.calculateDiff(weaponDiff)
        weaponsList = newList
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return WeaponViewHolder(layoutInflater.inflate(R.layout.reusable_card, parent, false))
    }

    override fun getItemCount(): Int = weaponsList.size

    override fun onBindViewHolder(holder: WeaponViewHolder, position: Int) {
        val item = weaponsList[position]
        holder.render(item, onClickListener)

    }
}