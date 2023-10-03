package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.domain.models.Article
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.cuervolu.witcherscodex.domain.models.Weapon
import com.cuervolu.witcherscodex.ui.dashboard.diffutils.ArticleDiffUtil
import com.cuervolu.witcherscodex.ui.weapons.WeaponsDiffUtil

class ArticleAdapter(var articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    // Clase interna que representa cada elemento en la lista
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txtCharacterName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.txtCharacterDesc)
        val articleImageView: ImageView = itemView.findViewById(R.id.ivCharacter)
    }

    fun updateList(newList: List<Article>) {
        val articleDiff = ArticleDiffUtil(articles, newList)
        val result = DiffUtil.calculateDiff(articleDiff)
        articles = newList
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        // Infla el diseño del elemento de lista y crea una instancia de ArticleViewHolder
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        // Configura las vistas dentro del elemento con los datos del artículo correspondiente
        val article = articles[position]
        holder.titleTextView.text = article.title
        holder.descriptionTextView.text = article.description
        // Cargar la imagen utilizando Glide
        Glide.with(holder.itemView.context)
            .load(article.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.articleImageView)
    }

    override fun getItemCount(): Int {
        // Devuelve el número total de elementos en la lista
        return articles.size
    }

    companion object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}
