package com.cuervolu.witcherscodex.ui.bestiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.databinding.FragmentEntryDetailBinding
import com.cuervolu.witcherscodex.domain.models.Bestiary
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EntryDetailFragment : Fragment() {

    companion object {
        fun newInstance(entryId: String): EntryDetailFragment {
            val fragment = EntryDetailFragment()
            val args = Bundle()
            args.putString("entryId", entryId)

            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: EntryDetailViewModel by viewModels()
    private lateinit var binding: FragmentEntryDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEntryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEntryDetailBinding.bind(view)

        viewModel.entryLiveData.observe(viewLifecycleOwner) { entry ->
            updateUI(entry)
        }

        // Obtener el ID de la entrada de los argumentos del fragmento
        val entryId: String? = arguments?.getString("entryId")

        entryId?.let {
            viewModel.loadEntry(it)
        }
    }


    private fun updateUI(entry: Bestiary) {

        Glide.with(binding.root)
            .load(entry.image)
            .placeholder(R.drawable.placeholder_4)
            .error(R.drawable.image_placeholder_error)
            .into(binding.monsterImageView)

        binding.monsterNameTextView.text = entry.name
        binding.monsterTypeTextView.text = entry.type
        binding.locationTextView.text = getString(R.string.location_placeholder, entry.location)
        binding.descriptionTextView.text = entry.desc
        binding.lootTextView.text =
            getString(R.string.loot_placeholder, entry.loot ?: "No loot")
        binding.weaknessTextView.text =
            getString(R.string.weakness_placeholder, entry.weakness ?: "No known weakness")
    }


}