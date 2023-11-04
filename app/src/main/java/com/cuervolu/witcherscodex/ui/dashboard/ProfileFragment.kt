package com.cuervolu.witcherscodex.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.core.dialog.ConfirmationDialog
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog
import com.cuervolu.witcherscodex.databinding.FragmentProfileBinding
import com.cuervolu.witcherscodex.domain.models.User
import com.cuervolu.witcherscodex.ui.login.LoginActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

interface LogoutListener {
    fun onLogout()
}

/**
 * Fragmento que representa el perfil del usuario en la aplicación.
 *
 * Este fragmento muestra información sobre el usuario, como su nombre y foto de perfil,
 * y permite al usuario realizar acciones como cerrar sesión.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var logoutListener: LogoutListener

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PROFILE_CACHE_KEY = "profile_data"
    }

    // Inyector para mostrar diálogos
    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    /**
     * Método llamado al crear la vista del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla la vista del fragmento
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = binding.root

        sharedPreferences =
            requireContext().getSharedPreferences(PROFILE_CACHE_KEY, Context.MODE_PRIVATE)

        val cachedProfileData = loadProfileDataFromCache()
        if (cachedProfileData != null) {
            setUserData(cachedProfileData)
        } else {
            profileViewModel.userData.observe(viewLifecycleOwner) { userData ->
                setUserData(userData)
                saveProfileDataToCache(userData)
            }
        }
        if (cachedProfileData == null) {
            profileViewModel.getUserData()
        }

        // Inicializa la interfaz de usuario
        initUI()
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutListener) {
            logoutListener = context
        } else {
            throw ClassCastException("$context must implement LogoutListener")
        }
    }

    private fun loadProfileDataFromCache(): User? {
        val cachedDataString = sharedPreferences.getString(PROFILE_CACHE_KEY, null)
        return if (cachedDataString != null) {
            try {
                val cachedData = Gson().fromJson(cachedDataString, User::class.java)
                cachedData
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private fun setUserData(userData: User) {
        binding.displayName.text = userData.realname
        binding.givenNameTextInput.editText?.setText(userData.realname)
        binding.usernameTextInput.editText?.setText(userData.nickname)
        loadProfileImage(userData.imageUrl)
    }

    /**
     * Inicializa la interfaz de usuario.
     */
    private fun initUI() {
        initListeners()
        initObservers()
    }

    /**
     * Inicializa los listeners de los elementos de la interfaz de usuario.
     */
    private fun initListeners() {
        binding.btnLogOut.setOnClickListener {
            profileViewModel.onLogoutConfirmationDialogShown(
                true
            )
        }
    }

    private fun saveProfileDataToCache(userData: User?) {
        val sharedPreferences =
            requireContext().getSharedPreferences("profile_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val userDataJson = Gson().toJson(userData)

        editor.putString(PROFILE_CACHE_KEY, userDataJson)
        editor.apply()
    }


    /**
     * Inicializa los observadores para los cambios en el ViewModel.
     */
    private fun initObservers() {
        profileViewModel.showLogoutConfirmation.observe(viewLifecycleOwner) { shouldShowConfirmation ->
            if (shouldShowConfirmation) {
                showLogoutConfirmationDialog { confirmed ->
                    if (confirmed) {
                        // Realiza el cierre de sesión cuando se confirma
                        lifecycleScope.launch { // Lanzar un coroutine
                            profileViewModel.onLogOutSelected()
                            profileViewModel.navigateToLogin
                        }
                    }
                    // Restablece el indicador de confirmación
                    profileViewModel.onLogoutConfirmationDialogShown(false) // Restablecer el indicador
                }
            }
        }
        // Observa si se debe mostrar un diálogo de error
        profileViewModel.showErrorDialog.observe(viewLifecycleOwner) { errorPair ->
            val (errorTitle, errorDescription) = errorPair
            showErrorDialog(errorTitle, errorDescription)
        }

        // Observa si se debe navegar a la pantalla de inicio de sesión
        profileViewModel.navigateToLogin.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                // Navega a la pantalla de inicio de sesión
                goToLogin()
            }
        }
    }

    /**
     * Muestra un diálogo de error.
     *
     * @param errorTitle Título del error.
     * @param errorDescription Descripción del error.
     */
    private fun showErrorDialog(errorTitle: String, errorDescription: String) {
        ErrorDialog.create(
            title = errorTitle,
            description = errorDescription,
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(requireActivity().supportFragmentManager, null)
    }

    /**
     * Muestra un diálogo de confirmación de cierre de sesión.
     *
     * @param callback Callback que se llama cuando se confirma o cancela el cierre de sesión.
     */
    private fun showLogoutConfirmationDialog(callback: (confirmed: Boolean) -> Unit) {
        val title = getString(R.string.logout_confirmation_title)
        val message = getString(R.string.logout_confirmation_message)

        val dialog = ConfirmationDialog.create(title, message)

        // Configura el callback para manejar la respuesta del diálogo
        dialog.setDialogCallback(callback)

        dialogLauncher.show(dialog, requireActivity())
    }

    /**
     * Carga la imagen de perfil del usuario.
     *
     * @param imageUrl URL de la imagen de perfil.
     */
    private fun loadProfileImage(imageUrl: String?) {
        if (imageUrl != null && imageUrl != "null") {
            Glide.with(requireContext()).clear(binding.profileImage)
            Glide.with(requireContext())
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.profileImage)
        } else {
            loadProfileImageWithPlaceholder()
        }
    }


    /**
     * Carga una imagen de perfil de Placeholder cuando la URL de la imagen es nula.
     */
    private fun loadProfileImageWithPlaceholder() {
        Glide.with(requireContext())
            .load(R.drawable.avatar_placeholder)
            .apply(RequestOptions.circleCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.profileImage)
    }

    /**
     * Navega a la pantalla de inicio de sesión.
     */
    private fun goToLogin() {
        startActivity(LoginActivity.create(requireActivity()))
        // Llama al método onLogout() de la interfaz para que la actividad principal se encargue de cerrar la actividad actual.
        logoutListener.onLogout()
    }

    /**
     * Se llama cuando se destruye la vista del fragmento.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}