package com.cuervolu.witcherscodex.adapters

import androidx.recyclerview.widget.DiffUtil
import com.cuervolu.witcherscodex.data.response.Datum

class StreamsDiffUtil(private val oldList: List<Datum>, private val newList: List<Datum>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
