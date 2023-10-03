package com.cuervolu.witcherscodex.ui.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.cuervolu.witcherscodex.ui.dashboard.DashboardActivity
import com.cuervolu.witcherscodex.ui.login.model.UserLogin
import com.cuervolu.witcherscodex.ui.signin.SignInActivity
import com.cuervolu.witcherscodex.ui.verification.VerificationActivity
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Actividad que permite a los usuarios iniciar sesión en la aplicación.
 *
 * Esta actividad proporciona una interfaz de usuario para que los usuarios ingresen su correo electrónico y contraseña,
 * y realiza el proceso de inicio de sesión utilizando el [LoginViewModel].
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
        initUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val credential =
                            googleClient.oneTapClient.getSignInCredentialFromIntent(data)
                        val idToken = credential.googleIdToken
                        when {
                            idToken != null -> {
                                // Got an ID token from Google. Use it to authenticate
                                // with Firebase.
                                val firebaseCredential =
                                    GoogleAuthProvider.getCredential(idToken, null)
                                firebaseClient.auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Timber.d("signInWithCredential:success")
                                            val user = firebaseClient.auth.currentUser
                                            loginViewModel.saveUserDataToFirestore(user)
                                            goToDashboard()
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Timber.w(
                                                "signInWithCredential:failure ${task.exception}",

                                                )
                                        }
                                    }

                            }
                        }
                    } catch (e: ApiException) {
                        Timber.e("An error occurred: ${e.message}")
                    }
                } else {
                    // Autenticación fallida, maneja el caso de error
                    Timber.d("Authentication failed with result code: $resultCode")
                }
            }
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


        binding.viewBottom.btnGoogle.setOnClickListener {
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
                    Timber.d(e.localizedMessage)
                }
        }
    }

    private fun initObservers() {
        loginViewModel.navigateToDetails.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToDashboard()
            }
        }

        loginViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToSignIn()
            }
        }

        loginViewModel.navigateToForgotPassword.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToForgotPassword()
            }
        }

        loginViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerify()
            }
        }

        loginViewModel.showErrorDialog.observe(this) { userLogin ->
            if (userLogin.showErrorDialog) showErrorDialog(userLogin)
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun updateUI(viewState: LoginViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            tilEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.login_error_mail)
            tilPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.login_error_password)
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

    private fun goToSignIn() {
        startActivity(SignInActivity.create(this))
    }

    private fun goToDashboard() {
        startActivity(DashboardActivity.create(this))
        finishAffinity()
        return
    }

    private fun goToVerify() {
        startActivity(VerificationActivity.create(this))
    }
}