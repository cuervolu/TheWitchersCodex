package com.cuervolu.witcherscodex.ui.bestiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.adapters.BestiaryAdapter
import com.cuervolu.witcherscodex.adapters.CharacterAdapter
import com.cuervolu.witcherscodex.databinding.FragmentBestiaryBinding
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BestiaryFragment : Fragment() {

    private lateinit var binding: FragmentBestiaryBinding
    private val bestiaryViewModel: BestiaryViewModel by viewModels()
    private val bestiaryAdapter = BestiaryAdapter { monster -> onItemSelected(monster) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBestiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBestiaryAdapter()
        getMonsters()
        setProgressBarAccordingToLoadState()
    }

    private fun getMonsters() {
        lifecycleScope.launch {
            bestiaryViewModel.flow.collectLatest {
                bestiaryAdapter.submitData(it)
            }
        }
    }

    private fun setBestiaryAdapter() {
        binding.rwBeastiary.layoutManager = LinearLayoutManager(requireContext())
        binding.rwBeastiary.adapter = bestiaryAdapter
    }

    private fun onItemSelected(monster: Bestiary) {
        Toast.makeText(requireContext(), monster.name, Toast.LENGTH_SHORT).show()
    }

    private fun setProgressBarAccordingToLoadState() {
        lifecycleScope.launch {
            bestiaryAdapter.loadStateFlow.collect { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading
                binding.progressBar.isVisible = isLoading
                binding.loadingLayout.isVisible = isLoading
            }
        }
    }

}
