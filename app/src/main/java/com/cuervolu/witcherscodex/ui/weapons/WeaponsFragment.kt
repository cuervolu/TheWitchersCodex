package com.cuervolu.witcherscodex.ui.weapons

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.adapters.WeaponAdapter
import com.cuervolu.witcherscodex.databinding.FragmentWeaponsBinding
import com.cuervolu.witcherscodex.domain.models.Character
import com.cuervolu.witcherscodex.domain.models.Weapon
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeaponsFragment : Fragment() {

    companion object {
        fun newInstance() = WeaponsFragment()
    }

    private lateinit var weaponAdapter: WeaponAdapter
    private lateinit var binding: FragmentWeaponsBinding
    private val viewModel: WeaponsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentWeaponsBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.weaponLiveData.observe(viewLifecycleOwner) { weapons ->
            weaponAdapter.weaponsList = weapons // Actualiza la lista en el adaptador
            weaponAdapter.updateList(weapons) // Notifica cambios en el adaptador
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // Mostrar el ShimmerFrameLayout
                binding.loadingLayout.visibility = View.VISIBLE
                binding.rwWeapons.visibility = View.GONE
            } else {
                // Mostrar el RecyclerView y ocultar el ShimmerFrameLayout
                binding.rwWeapons.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
            }
        }
        viewModel.loadWeapons()
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        weaponAdapter = WeaponAdapter(emptyList()) { character -> onItemSelected(character) }

        binding.rwWeapons.layoutManager = manager
        binding.rwWeapons.adapter = weaponAdapter
    }

    private fun onItemSelected(weapon: Weapon) {
        Toast.makeText(requireContext(), weapon.name, Toast.LENGTH_SHORT).show()
    }
}