package com.cuervolu.witcherscodex.ui.weapons

import androidx.recyclerview.widget.DiffUtil
import com.cuervolu.witcherscodex.domain.models.Weapon

class WeaponsDiffUtil(
    private val oldList: List<Weapon>,
    private val newList: List<Weapon>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}