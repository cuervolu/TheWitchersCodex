package com.cuervolu.witcherscodex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.domain.models.Character
import com.cuervolu.witcherscodex.ui.characters.CharactersDiffUtil

class CharacterAdapter(
    var charactersList: List<Character>,
    private val onClickListener: (Character) -> Unit
) :
    RecyclerView.Adapter<CharacterViewHolder>() {

    fun updateList(newList: List<Character>) {
        val characterDiff = CharactersDiffUtil(charactersList, newList)
        val result = DiffUtil.calculateDiff(characterDiff)
        charactersList = newList
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CharacterViewHolder(layoutInflater.inflate(R.layout.reusable_card, parent, false))
    }

    override fun getItemCount(): Int = charactersList.size

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val item = charactersList[position]
        holder.render(item, onClickListener)

        // Verifica si la profesión está presente y no está vacía
        if (item.personal_information?.profession.isNullOrEmpty()) {
            holder.binding.txtCharacterProfession.visibility = View.GONE
        } else {
            // Muestra el TextView de la profesión si hay datos
            holder.binding.txtCharacterProfession.visibility = View.VISIBLE
            holder.binding.txtCharacterProfession.text = item.personal_information?.profession
        }

    }

}