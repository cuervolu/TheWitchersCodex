package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.domain.models.FunFact

class FactsAdapter(
    private val lifecycle: Lifecycle,
    private val onClickListener: (FunFact) -> Unit
) : PagingDataAdapter<FunFact, FunFactViewHolder>(Companion) {
    private var originalList: List<FunFact>? = null
    private var filteredList: List<FunFact>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunFactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FunFactViewHolder(layoutInflater.inflate(R.layout.item_fact, parent, false))
    }

    override fun onBindViewHolder(holder: FunFactViewHolder, position: Int) {
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
            originalList?.filter { it.title.lowercase().contains(lowerCaseFilter) }
        }

        submitData(lifecycle, PagingData.from(filteredList ?: emptyList()))
    }

    companion object : DiffUtil.ItemCallback<FunFact>() {
        override fun areItemsTheSame(oldItem: FunFact, newItem: FunFact): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: FunFact, newItem: FunFact): Boolean {
            return oldItem == newItem
        }
    }
}