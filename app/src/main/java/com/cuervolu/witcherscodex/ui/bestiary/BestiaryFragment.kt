package com.cuervolu.witcherscodex.ui.bestiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.adapters.BestiaryAdapter
import com.cuervolu.witcherscodex.databinding.FragmentBestiaryBinding
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BestiaryFragment : Fragment() {

    private lateinit var binding: FragmentBestiaryBinding
    private lateinit var searchView: SearchView
    private lateinit var fab: FloatingActionButton
    private val bestiaryViewModel: BestiaryViewModel by viewModels()
    private lateinit var bestiaryAdapter: BestiaryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBestiaryBinding.inflate(inflater, container, false)
        fab = FloatingActionButton(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bestiaryAdapter =
            BestiaryAdapter(viewLifecycleOwner.lifecycle) { monster -> onItemSelected(monster) }
        setBestiaryAdapter()
        getMonsters()
        setProgressBarAccordingToLoadState()

        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val queryString = query ?: ""
                // Lanzar una coroutine para ejecutar la filtración en un contexto suspendido
                lifecycleScope.launch {
                    bestiaryAdapter.filter(queryString)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val queryString = newText ?: ""
                // Lanzar una coroutine para ejecutar la filtración en un contexto suspendido
                lifecycleScope.launch {
                    bestiaryAdapter.filter(queryString)
                }
                return false
            }
        })

        fab = binding.fab

        fab.setOnClickListener {
            replaceFragment(CreateMonsterFragment.newInstance())
        }
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
        replaceFragment(EntryDetailFragment.newInstance(monster.entryId))
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


    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}

