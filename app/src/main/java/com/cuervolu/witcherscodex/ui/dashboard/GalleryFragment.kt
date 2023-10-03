package com.cuervolu.witcherscodex.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.cuervolu.witcherscodex.adapters.CharacterAdapter
import com.cuervolu.witcherscodex.adapters.GalleryAdapter
import com.cuervolu.witcherscodex.databinding.FragmentGalleryBinding
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {
    companion object {
        fun newInstance() = GalleryFragment()
    }

    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var binding: FragmentGalleryBinding

    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Observa los cambios en characterLiveData
        viewModel.galleryLiveData.observe(viewLifecycleOwner) { image ->
            galleryAdapter.imageUrls = image // Actualiza la lista en el adaptador
            galleryAdapter.updateList(image) // Notifica cambios en el adaptador
        }

        initRecyclerView()
        viewModel.loadImages() // Inicia la carga de personajes desde el ViewModel
    }


    private fun initRecyclerView() {
        val manager = GridLayoutManager(context, 3)
        galleryAdapter = GalleryAdapter(emptyList()) { image -> onItemSelected(image) }
        binding.rwGallery.layoutManager = manager
        binding.rwGallery.adapter = galleryAdapter
    }

    private fun onItemSelected(image: String) {
        Toast.makeText(requireContext(), "Holi", Toast.LENGTH_SHORT).show()
    }

}