package com.cuervolu.witcherscodex.ui.characters

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
import com.cuervolu.witcherscodex.databinding.FragmentCreateCharacterBinding
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class CreateCharacterFragment : Fragment() {

    companion object {
        fun newInstance() = CreateCharacterFragment()
    }

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher
    private val viewModel: CreateCharacterViewModel by viewModels()
    private lateinit var binding: FragmentCreateCharacterBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChooseImage.setOnClickListener {
            openGallery()
        }

        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                selectedImageUri?.let {
                    viewModel.createCharacter(
                        name = binding.editTextName.text.toString(),
                        description = binding.editTextDescription.text.toString(),
                        alias = binding.editTextAlias.text.toString(),
                        hairColor = binding.editTextHairColor.text.toString(),
                        eyeColor = binding.editTextEyeColor.text.toString(),
                        race = binding.editTextRace.text.toString(),
                        skinColor = binding.editTextSkin.text.toString(),
                        gender = binding.editTextGender.text.toString(),
                        nationality = binding.editTextNationality.text.toString(),
                        born = binding.editTextBorn.text.toString(),
                        it
                    )
                }
            }
        }

        viewModel.createCharacterResult.observe(viewLifecycleOwner) { result ->
            handleCreateMonsterResult(result)
        }
    }

    private fun handleCreateMonsterResult(result: Boolean) {
        if (result) {
            binding.btnSubmit.isEnabled = false
            Toast.makeText(
                requireContext(),
                getString(R.string.entry_created_confirmation_title),
                Toast.LENGTH_SHORT
            ).show()
            replaceFragment(CharactersFragment())
            Glide.with(requireContext()).clear(binding.imagePreview)

        } else {
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

        if (binding.editTextAlias.text.isNullOrBlank()) {
            binding.textInputLayoutAlias.error = "Alias is required"
            isValid = false
        } else {
            binding.textInputLayoutAlias.error = null
        }


        if (binding.editTextHairColor.text.isNullOrBlank()) {
            binding.textInputLayoutHairColor.error = "Hair Color is required"
            isValid = false
        } else {
            binding.textInputLayoutHairColor.error = null
        }


        if (binding.editTextEyeColor.text.isNullOrBlank()) {
            binding.textInputLayoutEyeColor.error = "Eye Color is required"
            isValid = false
        } else {
            binding.textInputLayoutEyeColor.error = null
        }

        if (binding.editTextNationality.text.isNullOrBlank()) {
            binding.textInputLayoutNationality.error = "Nationality is required"
            isValid = false
        } else {
            binding.textInputLayoutEyeColor.error = null
        }

        if (binding.editTextSkin.text.isNullOrBlank()) {
            binding.textInputLayoutSkin.error = "Skin is required"
            isValid = false
        } else {
            binding.textInputLayoutEyeColor.error = null
        }

        if (binding.editTextBorn.text.isNullOrBlank()) {
            binding.textInputLayoutBorn.error = "Born is required"
            isValid = false
        } else {
            binding.textInputLayoutBorn.error = null
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
    }
}
