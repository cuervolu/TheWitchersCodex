package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.domain.models.Bestiary

class BestiaryAdapter(
    private val onClickListener: (Bestiary) -> Unit
) :  PagingDataAdapter<Bestiary, BestiaryViewHolder>(Companion) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestiaryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BestiaryViewHolder(layoutInflater.inflate(R.layout.reusable_card, parent, false))
    }

    override fun onBindViewHolder(holder: BestiaryViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.render(item, onClickListener)
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
