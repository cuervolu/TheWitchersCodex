package com.cuervolu.witcherscodex.ui.bestiary

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
import com.cuervolu.witcherscodex.databinding.FragmentEntryDetailBinding
import com.cuervolu.witcherscodex.domain.models.Bestiary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
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
            replaceFragment(UpdateEntryFragment.newInstance(entryId!!))
        }

        viewModel.deleteMonsterResult.observe(viewLifecycleOwner) { result ->
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
            replaceFragment(BestiaryFragment())

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

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}