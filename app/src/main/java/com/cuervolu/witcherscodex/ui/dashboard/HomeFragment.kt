package com.cuervolu.witcherscodex.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.adapters.ArticleAdapter
import com.cuervolu.witcherscodex.adapters.BestiaryFeaturedAdapter
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog
import com.cuervolu.witcherscodex.databinding.FragmentHomeBinding
import com.cuervolu.witcherscodex.ui.bestiary.BestiaryFragment
import com.cuervolu.witcherscodex.ui.characters.CharactersFragment
import com.cuervolu.witcherscodex.ui.weapons.WeaponsFragment
import com.github.ybq.android.spinkit.SpinKitView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    // ViewModel asociado a este fragmento
    private val homeViewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var articleLoadingIndicator: SpinKitView
    private lateinit var bestiaryLoadingIndicator: SpinKitView


    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var beastRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var bestiaryFeaturedAdapter: BestiaryFeaturedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Infla el diseño del fragmento y configura el binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        val bestiaryButton = root.findViewById<View>(R.id.bestiary_button)
        bestiaryButton.setOnClickListener {
            homeViewModel.onBestiarySelected()
        }

        val charactersButton = root.findViewById<View>(R.id.character_button)
        charactersButton.setOnClickListener {
            homeViewModel.onCharactersSelected()
        }

        val weaponsButton = root.findViewById<View>(R.id.weapons_button)
        weaponsButton.setOnClickListener {
            homeViewModel.onWeaponsSelected()
        }


        articleLoadingIndicator = root.findViewById(R.id.articleLoadingIndicator)
        articleLoadingIndicator.visibility = View.VISIBLE
        bestiaryLoadingIndicator = root.findViewById(R.id.bestiaryLoadingIndicator)
        bestiaryLoadingIndicator.visibility = View.VISIBLE

        //Cargar los RecylerView
        articlesRecyclerView = root.findViewById(R.id.articleRecyclerView)
        articlesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        articleAdapter = ArticleAdapter(emptyList()) // Inicialmente, el adaptador está vacío
        articlesRecyclerView.adapter = articleAdapter

        beastRecyclerView = root.findViewById(R.id.bestiaryRecyclerView)
        beastRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        bestiaryFeaturedAdapter = BestiaryFeaturedAdapter(emptyList())
        beastRecyclerView.adapter = bestiaryFeaturedAdapter


        homeViewModel.loadFeaturedArticles()
        homeViewModel.loadTrendingBeastEntries()

        homeViewModel.featuredArticles.observe(viewLifecycleOwner) { articles ->
            // Actualiza el adaptador con la nueva lista de artículos
            articleAdapter.articles = articles
            articleLoadingIndicator.visibility = View.GONE
            articleAdapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
        }
        homeViewModel.trendingBeast.observe(viewLifecycleOwner) { entries ->
            bestiaryFeaturedAdapter.bestiary = entries
            bestiaryLoadingIndicator.visibility = View.GONE
            bestiaryFeaturedAdapter.notifyDataSetChanged()
        }

        homeViewModel.showErrorDialog.observe(viewLifecycleOwner) { showError ->
            if (showError) showErrorDialog()
        }

        homeViewModel.navigateToBestiary.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                // Navega al fragmento del bestiario
                val fragment = BestiaryFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null) // Opcional: agrega a la pila de retroceso
                transaction.commit()

                // Reinicia el estado de navegación
                homeViewModel.onBestiaryNavigated()
            }
        }

        homeViewModel.navigateToWeapon.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val fragment = WeaponsFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null) // Opcional: agrega a la pila de retroceso
                transaction.commit()

                // Reinicia el estado de navegación
                homeViewModel.onWeaponsNavigated()
            }
        }

        homeViewModel.navigateToCharacters.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val fragment = CharactersFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()

                homeViewModel.onCharactersNavigated()
            }
        }
        return root
    }

    private fun showErrorDialog() {
        ErrorDialog.create(title = getString(R.string.signin_error_dialog_title),
            description = getString(R.string.signin_error_dialog_body),
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }).show(requireActivity().supportFragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
