package com.cuervolu.witcherscodex.ui.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog
import com.cuervolu.witcherscodex.core.ex.dismissKeyboard
import com.cuervolu.witcherscodex.core.ex.loseFocusAfterAction
import com.cuervolu.witcherscodex.core.ex.onTextChanged
import com.cuervolu.witcherscodex.core.ex.show
import com.cuervolu.witcherscodex.core.ex.span
import com.cuervolu.witcherscodex.core.ex.toast
import com.cuervolu.witcherscodex.data.network.FirebaseClient
import com.cuervolu.witcherscodex.data.network.GoogleClient
import com.cuervolu.witcherscodex.databinding.ActivityLoginBinding
import com.cuervolu.witcherscodex.ui.login.model.UserLogin
import com.cuervolu.witcherscodex.utils.LoginActivityHelper
import com.cuervolu.witcherscodex.utils.MusicPlayerManager

import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Actividad que permite a los usuarios iniciar sesión en la aplicación.
 *
 * Esta actividad proporciona una interfaz de usuario para que los usuarios ingresen su correo electrónico y contraseña,y realiza el proceso de inicio de sesión utilizando el [LoginViewModel].
 * También gestiona la navegación a otras pantallas, como la pantalla de registro o la pantalla de restablecimiento de contraseña.
 *
 * @property binding Referencia a la vista de diseño de la actividad.
 * @property loginViewModel ViewModel que maneja la lógica de inicio de sesión.
 * @property dialogLauncher Clase utilizada para mostrar diálogos personalizados.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val REQ_ONE_TAP = 2

    companion object {
        fun create(context: Context): Intent =
            Intent(context, LoginActivity::class.java)

    }

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var loginHelper: LoginActivityHelper

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    @Inject
    lateinit var googleClient: GoogleClient

    @Inject
    lateinit var firebaseClient: FirebaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginHelper = LoginActivityHelper(loginViewModel, googleClient, firebaseClient)
        initUI()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> loginHelper.handleOneTapResult(resultCode, data, this)
        }
    }


    private fun initUI() {
        initListeners()
        initObservers()
        binding.viewBottom.tvFooter.text = span(
            getString(R.string.login_footer_unselected),
            getString(R.string.login_footer_selected)
        )
    }

    private fun initListeners() {
        binding.viewBottom.btnGoogle.setOnClickListener {
            Timber.d("Google button clicked")
            // Utiliza el cliente de Google para iniciar la autenticación con One Tap UI
            googleClient.oneTapClient.beginSignIn(googleClient.signUpRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Timber.e("Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // En caso de error, puedes manejarlo aquí
                    e.localizedMessage?.let { it1 -> Timber.d(it1) }
                }
        }
        binding.etEmail.onTextChanged { text ->
            loginHelper.onFieldChanged(
                text,
                binding.etPassword.text.toString()
            )
        }
        binding.etPassword.onTextChanged { text ->
            loginHelper.onFieldChanged(
                binding.etEmail.text.toString(),
                text
            )
        }
        binding.etEmail.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etEmail.onTextChanged { onFieldChanged() }

        binding.etPassword.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.etPassword.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etPassword.onTextChanged { onFieldChanged() }

        binding.tvForgotPassword.setOnClickListener { loginViewModel.onForgotPasswordSelected() }

        binding.viewBottom.tvFooter.setOnClickListener { loginViewModel.onSignInSelected() }

        binding.btnLogin.setOnClickListener {
            it.dismissKeyboard()
            loginViewModel.onLoginSelected(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
    }

    private fun initObservers() {
        loginViewModel.navigateToDetails.observe(this) {
            it.getContentIfNotHandled()?.let {
                loginHelper.goToDashboard(this)
            }
        }

        loginViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let {
                loginHelper.goToSignIn(this@LoginActivity)
            }
        }

        loginViewModel.navigateToForgotPassword.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToForgotPassword()
            }
        }

        loginViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                loginHelper.goToVerify(this@LoginActivity)
            }
        }

        loginViewModel.showErrorDialog.observe(this) { userLogin ->
            if (userLogin.showErrorDialog) showErrorDialog(userLogin)
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.viewState.collect { viewState ->
                loginHelper.updateUI(this@LoginActivity, viewState, binding)
            }
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            loginViewModel.onFieldsChanged(
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString()
            )
        }
    }

    private fun showErrorDialog(userLogin: UserLogin) {
        ErrorDialog.create(
            title = getString(R.string.login_error_dialog_title),
            description = getString(R.string.login_error_dialog_body),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                loginViewModel.onLoginSelected(
                    userLogin.email,
                    userLogin.password
                )
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }


    override fun onResume() {
        super.onResume()

        // Reanuda la reproducción de música
        MusicPlayerManager.resumePlaying()
    }

    override fun onPause() {
        super.onPause()
        // Pausa la reproducción de música
        MusicPlayerManager.pausePlaying()
    }
    private fun goToForgotPassword() {
        toast(getString(R.string.feature_not_allowed))
    }

}