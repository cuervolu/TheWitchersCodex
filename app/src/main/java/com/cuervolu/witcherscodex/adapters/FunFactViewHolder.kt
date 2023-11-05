package com.cuervolu.witcherscodex.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.databinding.ItemFactBinding
import com.cuervolu.witcherscodex.domain.models.FunFact
import com.cuervolu.witcherscodex.utils.GlideImageLoader

class FunFactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemFactBinding.bind(view)

    fun render(factModel: FunFact, onClickListener: (FunFact) -> Unit) {
        GlideImageLoader.loadImage(binding.ivFact, factModel.imageUrl)
        itemView.setOnClickListener {
            onClickListener(factModel)
        }
    }
}