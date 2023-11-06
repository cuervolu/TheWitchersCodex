package com.cuervolu.witcherscodex.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.adapters.FactsAdapter
import com.cuervolu.witcherscodex.databinding.FragmentFactsBinding
import com.cuervolu.witcherscodex.domain.models.FunFact
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FactsFragment : Fragment() {

    companion object {
        fun newInstance() = FactsFragment()
    }

    private val viewModel: FactsViewModel by viewModels()

    private lateinit var binding: FragmentFactsBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var  factsAdapter: FactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFactsBinding.inflate(inflater, container, false)
        fab = FloatingActionButton(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        factsAdapter = FactsAdapter(viewLifecycleOwner.lifecycle) { fact -> onItemSelected(fact) }
        setFactsAdapter()
        getFacts()
        setProgressBarAccordingToLoadState()
    }


    private fun getFacts() {
        lifecycleScope.launch {
            viewModel.flow.collectLatest {
                factsAdapter.submitData(it)
            }
        }
    }

    private fun setFactsAdapter() {
        binding.rwFacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rwFacts.adapter = factsAdapter
    }

    private fun onItemSelected(fact: FunFact) {
        Toast.makeText(requireContext(), fact.title, Toast.LENGTH_SHORT).show()
    }

    private fun setProgressBarAccordingToLoadState() {
        lifecycleScope.launch {
            factsAdapter.loadStateFlow.collect { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading
                binding.progressBar.isVisible = isLoading
//                binding.loadingLayout.isVisible = isLoading
            }
        }
    }


}