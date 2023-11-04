package com.cuervolu.witcherscodex.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.data.response.Datum
import com.cuervolu.witcherscodex.databinding.ItemStreamBinding
import com.cuervolu.witcherscodex.utils.GlideImageLoader

class StreamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemStreamBinding.bind(view)

    fun render(streamModel: Datum, onClickListener: (Datum) -> Unit) {
        binding.txtStreamTitle.text = streamModel.title
        binding.txtStreamUsername.text = streamModel.userName
        val watchingCountString =
            itemView.context.getString(R.string.watching_count, streamModel.viewerCount.toString())
        binding.txtStreamViewerCount.text = watchingCountString

        val thumbnailURL = generateThumbnailURL(streamModel.thumbnailURL, 1000, 1000)

        GlideImageLoader.loadImage(binding.ivStream, thumbnailURL)
        itemView.setOnClickListener {
            onClickListener(streamModel)
        }
    }

    private fun generateThumbnailURL(originalURL: String, width: Int, height: Int): String {
        return originalURL.replace("{width}", width.toString()).replace("{height}", height.toString())
    }
}
