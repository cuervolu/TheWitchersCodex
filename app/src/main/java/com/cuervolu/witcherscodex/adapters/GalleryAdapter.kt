package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.ui.dashboard.diffutils.GalleryDiffUtil

class GalleryAdapter(var imageUrls: List<String>, private val onClickListener: (String) -> Unit):  RecyclerView.Adapter<GalleryViewHolder>()  {
    fun updateList(newList: List<String>) {
        val characterDiff = GalleryDiffUtil(imageUrls, newList)
        val result = DiffUtil.calculateDiff(characterDiff)
        imageUrls = newList
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GalleryViewHolder(layoutInflater.inflate(R.layout.bestiary_card_design, parent, false))
    }

    override fun getItemCount(): Int = imageUrls.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = imageUrls[position]
        holder.render(item, onClickListener)

    }
}
