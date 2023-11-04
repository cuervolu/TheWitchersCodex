package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.data.response.Datum
import com.cuervolu.witcherscodex.ui.dashboard.diffutils.ArticleDiffUtil

class StreamAdapter (
    var streamsList: List<Datum>,
    private val onClickListener: (Datum) -> Unit): RecyclerView.Adapter<StreamViewHolder>() {

    fun updateList(newList: List<Datum>) {
        val streamDiffUtil = StreamsDiffUtil(streamsList, newList)
        val result = DiffUtil.calculateDiff(streamDiffUtil)
        streamsList = newList
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return StreamViewHolder(layoutInflater.inflate(R.layout.item_stream, parent, false))
    }

    override fun getItemCount(): Int = streamsList.size

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        val item = streamsList[position]
        holder.render(item, onClickListener)

    }

    companion object : DiffUtil.ItemCallback<Datum>() {
        override fun areItemsTheSame(oldItem: Datum, newItem: Datum): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Datum, newItem: Datum): Boolean {
            return oldItem == newItem
        }
    }
}