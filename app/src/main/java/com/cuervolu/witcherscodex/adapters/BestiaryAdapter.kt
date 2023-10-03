package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.domain.models.Bestiary

class BestiaryAdapter(
    private val lifecycle: Lifecycle,
    private val onClickListener: (Bestiary) -> Unit
) : PagingDataAdapter<Bestiary, BestiaryViewHolder>(Companion) {

    private var originalList: List<Bestiary>? = null
    private var filteredList: List<Bestiary>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestiaryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BestiaryViewHolder(layoutInflater.inflate(R.layout.reusable_card, parent, false))
    }

    override fun onBindViewHolder(holder: BestiaryViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.render(item, onClickListener)
    }

    fun filter(filter: String) {
        if (originalList == null) {
            originalList = snapshot().items
        }

        val lowerCaseFilter = filter.lowercase()

        filteredList = if (filter.isEmpty()) {
            originalList
        } else {
            originalList?.filter { it.name.lowercase().contains(lowerCaseFilter) }
        }

        submitData(lifecycle, PagingData.from(filteredList ?: emptyList()))
    }


    companion object : DiffUtil.ItemCallback<Bestiary>() {
        override fun areItemsTheSame(oldItem: Bestiary, newItem: Bestiary): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Bestiary, newItem: Bestiary): Boolean {
            return oldItem == newItem
        }
    }
}
