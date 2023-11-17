package com.cuervolu.witcherscodex.ui.characters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.core.dialog.ConfirmationDialog
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog
import com.cuervolu.witcherscodex.databinding.FragmentCharacterDetailBinding
import com.cuervolu.witcherscodex.domain.models.Character
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CharacterDetailFragment : Fragment() {

    companion object {
        fun newInstance(entryId: String): CharacterDetailFragment {
            val fragment = CharacterDetailFragment()
            val args = Bundle()
            args.putString("entryId", entryId)

            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: CharacterDetailViewModel by viewModels()
    private lateinit var binding: FragmentCharacterDetailBinding

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCharacterDetailBinding.bind(view)

        viewModel.entryLiveData.observe(viewLifecycleOwner) { entry ->
            updateUI(entry)
        }

        // Obtener el ID de la entrada de los argumentos del fragmento
        val entryId: String? = arguments?.getString("entryId")
        Timber.d("entryId: $entryId")
        entryId?.let {
            viewModel.loadEntry(it)
        }

        binding.fabDelete.setOnClickListener {
            showConfirmationDialog { confirmed ->
                if (confirmed) {
                    entryId?.let {
                        viewModel.deleteEntry(it)
                    }
                }
            }
        }

        binding.fabEdit.setOnClickListener {
            replaceFragment(UpdateCharacterFragment.newInstance(entryId!!))
        }

        viewModel.deleteCharacterResult.observe(viewLifecycleOwner) { result ->
            handleEditMonsterResult(result)
        }
    }

    private fun handleEditMonsterResult(result: Boolean) {
        if (result) {
            Toast.makeText(
                requireContext(),
                getString(R.string.entry_delete_confirmation_title),
                Toast.LENGTH_SHORT
            ).show()
            replaceFragment(CharactersFragment())

        } else {
            showErrorDialog(
                getString(R.string.create_monster_error_dialog_title),
                getString(R.string.create_monster_error_dialog_description)
            )

        }
    }


    private fun showConfirmationDialog(callback: (confirmed: Boolean) -> Unit) {
        val title = getString(R.string.edit_entry_confirmation_title)
        val message = getString(R.string.edit_entry_confirmation_message)

        val dialog = ConfirmationDialog.create(title, message)

        // Configura el callback para manejar la respuesta del diálogo
        dialog.setDialogCallback(callback)

        dialogLauncher.show(dialog, requireActivity())
    }

    private fun showErrorDialog(errorTitle: String, errorDescription: String) {
        ErrorDialog.create(
            title = errorTitle,
            description = errorDescription,
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(requireActivity().supportFragmentManager, null)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(entry: Character) {

        Glide.with(binding.root)
            .load(entry.image_url)
            .placeholder(R.drawable.placeholder_2)
            .error(R.drawable.image_placeholder_error)
            .into(binding.monsterImageView)

        binding.monsterNameTextView.text = entry.name
        binding.characterProfessionTextView.text =
            entry.personal_information?.profession ?: "Unknown"
        binding.AgeTextView.text = "Age: ${entry.age}"
        binding.descriptionTextView.text = entry.description
        binding.BornTextView.text = "Born: ${entry.basic_information?.born ?: "Unknown"}"
        binding.EyeColorTextView.text =
            "Eye Color: ${entry.basic_information?.eye_color ?: "Unknown"}"
        binding.HairColorTextView.text =
            "Hair Color: ${entry.basic_information?.hair_color ?: "Unknown"}"
        binding.HeightTextView.text = "Height: ${entry.basic_information?.height ?: "Unknown"}"
        binding.SkinTextView.text = "Skin: ${entry.basic_information?.skin ?: "Unknown"}"
        binding.NationalityTextView.text =
            "Nationality: ${entry.basic_information?.nationality ?: "Unknown"}"
        binding.GenderTextView.text = "Gender: ${entry.basic_information?.gender ?: "Unknown"}"
        binding.RaceTextView.text = "Race: ${entry.basic_information?.race ?: "Unknown"}"
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}