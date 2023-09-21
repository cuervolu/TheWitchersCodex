package com.cuervolu.witcherscodex.ui.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuervolu.witcherscodex.adapters.CharacterAdapter
import com.cuervolu.witcherscodex.databinding.FragmentCharactersBinding
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CharactersFragment : Fragment() {

    companion object {
        fun newInstance() = CharactersFragment()
    }

    private val viewModel: CharactersViewModel by viewModels()
    private lateinit var binding: FragmentCharactersBinding
    private lateinit var charactersAdapter: CharacterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Observa los cambios en characterLiveData
        viewModel.characterLiveData.observe(viewLifecycleOwner) { characters ->
            charactersAdapter.charactersList = characters // Actualiza la lista en el adaptador
            charactersAdapter.updateList(characters) // Notifica cambios en el adaptador
        }

        // Observa la variable isLoading para controlar la visibilidad
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // Mostrar el ShimmerFrameLayout
                binding.loadingLayout.visibility = View.VISIBLE
                binding.recyclerCharacters.visibility = View.GONE
            } else {
                // Mostrar el RecyclerView y ocultar el ShimmerFrameLayout
                binding.recyclerCharacters.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
            }
        }

        viewModel.loadCharacters() // Inicia la carga de personajes desde el ViewModel
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        val decoration = DividerItemDecoration(requireContext(), manager.orientation)
        charactersAdapter = CharacterAdapter(emptyList()) { character -> onItemSelected(character) }
        binding.recyclerCharacters.layoutManager = manager
        binding.recyclerCharacters.adapter = charactersAdapter
//        binding.recyclerCharacters.addItemDecoration(decoration)
    }

    private fun onItemSelected(character: Character) {
        Toast.makeText(requireContext(), character.name, Toast.LENGTH_SHORT).show()
    }
}
