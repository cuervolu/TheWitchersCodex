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
import com.cuervolu.witcherscodex.adapters.StreamAdapter
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog
import com.cuervolu.witcherscodex.data.response.Datum
import com.cuervolu.witcherscodex.databinding.FragmentHomeBinding
import com.cuervolu.witcherscodex.ui.bestiary.BestiaryFragment
import com.cuervolu.witcherscodex.ui.characters.CharactersFragment
import com.cuervolu.witcherscodex.ui.weapons.WeaponsFragment
import com.github.ybq.android.spinkit.SpinKitView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    // ViewModel asociado a este fragmento
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleLoadingIndicator: SpinKitView
    private lateinit var bestiaryLoadingIndicator: SpinKitView


    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var streamsRecyclerView: RecyclerView
    private lateinit var beastRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var streamAdapter: StreamAdapter
    private lateinit var bestiaryFeaturedAdapter: BestiaryFeaturedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Infla el diseño del fragmento y configura el binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        setupButtons(root)
        setupLoadingIndicators(root)
        setupRecyclerViews(root)
        observeViewModelData()

        return root
    }

    private fun setupButtons(root: View) {
        val bestiaryButton = root.findViewById<View>(R.id.bestiary_button)
        bestiaryButton.setOnClickListener {
            viewModel.onBestiarySelected()
        }

        val charactersButton = root.findViewById<View>(R.id.character_button)
        charactersButton.setOnClickListener {
            viewModel.onCharactersSelected()
        }

        val weaponsButton = root.findViewById<View>(R.id.weapons_button)
        weaponsButton.setOnClickListener {
            viewModel.onWeaponsSelected()
        }
    }

    private fun setupLoadingIndicators(root: View) {
        articleLoadingIndicator = root.findViewById(R.id.articleLoadingIndicator)
        articleLoadingIndicator.visibility = View.VISIBLE
        bestiaryLoadingIndicator = root.findViewById(R.id.bestiaryLoadingIndicator)
        bestiaryLoadingIndicator.visibility = View.VISIBLE
    }


    private fun setupRecyclerViews(root: View) {
        streamsRecyclerView = root.findViewById(R.id.streamsRecyclerView)
        streamsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        streamAdapter =
            StreamAdapter(emptyList()) { stream -> onItemSelected(stream) }
        streamsRecyclerView.adapter = streamAdapter

        articlesRecyclerView = root.findViewById(R.id.articleRecyclerView)
        articlesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        articleAdapter = ArticleAdapter(emptyList())
        articlesRecyclerView.adapter = articleAdapter

        beastRecyclerView = root.findViewById(R.id.bestiaryRecyclerView)
        beastRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        bestiaryFeaturedAdapter = BestiaryFeaturedAdapter(emptyList())
        beastRecyclerView.adapter = bestiaryFeaturedAdapter
    }

    private fun onItemSelected(stream: Datum) {
        TODO("Not yet implemented")
    }

    private fun observeViewModelData() {
        viewModel.loadFeaturedArticles()
        viewModel.loadTrendingBeastEntries()
        viewModel.searchStreamsByName("streams?game_id=115977&language=en")

        viewModel.featuredArticles.observe(viewLifecycleOwner) { articles ->
            articleAdapter.articles = articles
            articleLoadingIndicator.visibility = View.GONE
            articleAdapter.updateList(articles)
        }

        viewModel.streams.observe(viewLifecycleOwner) { streams ->
            streamAdapter.streamsList = streams
            streamAdapter.notifyDataSetChanged()
        }

        viewModel.trendingBeast.observe(viewLifecycleOwner) { entries ->
            bestiaryFeaturedAdapter.bestiary = entries
            bestiaryLoadingIndicator.visibility = View.GONE
            bestiaryFeaturedAdapter.updateList(entries)
        }

        viewModel.showErrorDialog.observe(viewLifecycleOwner) { showError ->
            if (showError) showErrorDialog()
        }

        viewModel.navigateToBestiary.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                replaceFragment(BestiaryFragment())
                viewModel.onBestiaryNavigated()
            }
        }

        viewModel.navigateToWeapon.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                replaceFragment(WeaponsFragment())
                viewModel.onWeaponsNavigated()
            }
        }

        viewModel.navigateToCharacters.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                replaceFragment(CharactersFragment())
                viewModel.onCharactersNavigated()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showErrorDialog() {
        ErrorDialog.create(title = getString(R.string.signin_error_dialog_title),
            description = getString(R.string.signin_error_dialog_body),
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }).show(requireActivity().supportFragmentManager, null)
    }

}
