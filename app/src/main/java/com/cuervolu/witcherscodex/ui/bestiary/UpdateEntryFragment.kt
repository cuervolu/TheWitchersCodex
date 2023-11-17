package com.cuervolu.witcherscodex.ui.bestiary

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog
import com.cuervolu.witcherscodex.databinding.FragmentUpdateEntryBinding
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class UpdateEntryFragment : Fragment() {

    companion object {
        fun newInstance(entryId: String): UpdateEntryFragment {
            val fragment = UpdateEntryFragment()
            val args = Bundle()
            args.putString("entryId", entryId)

            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
    private val viewModel: UpdateEntryViewModel by viewModels()
    private lateinit var binding: FragmentUpdateEntryBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.entryLiveData.observe(viewLifecycleOwner) { entry ->
            setFields(entry)
        }

        // Obtener el ID de la entrada de los argumentos del fragmento
        val entryId: String? = arguments?.getString("entryId")

        entryId?.let {
            viewModel.loadEntry(it)
        }


        binding.btnChooseImage.setOnClickListener {
            openGallery()
        }

        binding.btnSubmit.setOnClickListener {
            selectedImageUri?.let {
                viewModel.updateMonster(
                    entryId!!,
                    binding.editTextName.text.toString(),
                    binding.editTextDescription.text.toString(),
                    binding.editTextLocation.text.toString(),
                    binding.editTextType.text.toString(),
                    binding.editTextLoot.text.toString(),
                    binding.editTextWeakness.text.toString(),
                    it,
                )
            }
        }

        viewModel.createMonsterResult.observe(viewLifecycleOwner) { result ->
            handleCreateMonsterResult(result)
        }
    }

    private fun setFields(entry: Bestiary) {
        selectedImageUri = Uri.parse(entry.image)
        binding.editTextName.setText(entry.name)
        binding.editTextDescription.setText(entry.desc)
        binding.editTextLocation.setText(entry.location)
        binding.editTextType.setText(entry.type)
        binding.editTextLoot.setText(entry.loot)
        binding.editTextWeakness.setText(entry.weakness)

        Glide.with(binding.root)
            .load(selectedImageUri)
            .placeholder(R.drawable.placeholder_4)
            .error(R.drawable.image_placeholder_error)
            .into(binding.imagePreview)
    }

    private fun handleCreateMonsterResult(result: Boolean) {
        if (result) {
            binding.btnSubmit.isEnabled = false
            binding.spinKit.visibility = View.VISIBLE
            Toast.makeText(
                requireContext(),
                getString(R.string.entry_update_confirmation_title),
                Toast.LENGTH_SHORT
            ).show()
            replaceFragment(BestiaryFragment())
            Glide.with(requireContext()).clear(binding.imagePreview)

        } else {
            binding.spinKit.visibility = View.GONE
            showErrorDialog(
                getString(R.string.create_monster_error_dialog_title),
                getString(R.string.create_monster_error_dialog_description)
            )
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                startUCrop(uri)
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            handleCropResult(data)
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped"))

        val options = UCrop.Options().apply {
            setCompressionQuality(70)
            setHideBottomControls(false)
            setFreeStyleCropEnabled(true)
            setToolbarTitle("Crop Image")
        }

        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .start(requireContext(), this)
    }

    private fun handleCropResult(result: Intent?) {
        val resultUri = UCrop.getOutput(result!!)
        selectedImageUri = resultUri
        // Muestra la vista previa de la imagen utilizando Glide
        Glide.with(this)
            .load(resultUri)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(binding.imagePreview)

        // Haz visible la vista previa de la imagen
        binding.imagePreview.visibility = View.VISIBLE
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.editTextName.text.isNullOrBlank()) {
            binding.textInputLayoutName.error = "Name is required"
            isValid = false
        } else {
            binding.textInputLayoutName.error = null
        }

        if (binding.editTextDescription.text.isNullOrBlank()) {
            binding.textInputLayoutDescription.error = "Description is required"
            isValid = false
        } else {
            binding.textInputLayoutDescription.error = null
        }

        if (binding.editTextLocation.text.isNullOrBlank()) {
            binding.textInputLayoutLocation.error = "Location is required"
            isValid = false
        } else {
            binding.textInputLayoutLocation.error = null
        }

        if (binding.editTextType.text.isNullOrBlank()) {
            binding.textInputLayoutType.error = "Type is required"
            isValid = false
        } else {
            binding.textInputLayoutType.error = null
        }

        return isValid
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

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
        binding.spinKit.visibility = View.GONE
    }
}
