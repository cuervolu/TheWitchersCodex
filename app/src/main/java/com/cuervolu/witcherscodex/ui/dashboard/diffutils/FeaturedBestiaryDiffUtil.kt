package com.cuervolu.witcherscodex.ui.dashboard.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.cuervolu.witcherscodex.domain.models.Bestiary

class FeaturedBestiaryDiffUtil(private val oldList: List<Bestiary>,
private val newList: List<Bestiary>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}