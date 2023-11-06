package com.cuervolu.witcherscodex.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.databinding.ItemFactBinding
import com.cuervolu.witcherscodex.domain.models.FunFact
import com.cuervolu.witcherscodex.utils.GlideImageLoader

class FunFactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemFactBinding.bind(view)

    fun render(factModel: FunFact, onClickListener: (FunFact) -> Unit) {
        binding.tvFactTitle.text = factModel.title
        GlideImageLoader.loadImage(binding.ivFact, factModel.imageUrl)


        binding.ivFact.setOnClickListener {
            val isExpanded = binding.tvFactTitle.visibility == View.VISIBLE
            if (isExpanded) {
                binding.tvFactTitle.visibility = View.GONE
            } else {
                binding.tvFactTitle.visibility = View.VISIBLE
            }
        }
        itemView.setOnClickListener {
            onClickListener(factModel)
        }

    }
}