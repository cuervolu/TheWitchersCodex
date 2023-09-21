package com.cuervolu.witcherscodex.ui.signin

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.core.Event
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog
import com.cuervolu.witcherscodex.core.ex.dismissKeyboard
import com.cuervolu.witcherscodex.core.ex.loseFocusAfterAction
import com.cuervolu.witcherscodex.core.ex.onTextChanged
import com.cuervolu.witcherscodex.core.ex.show
import com.cuervolu.witcherscodex.core.ex.span
import com.cuervolu.witcherscodex.data.network.FirebaseClient
import com.cuervolu.witcherscodex.data.network.GoogleClient
import com.cuervolu.witcherscodex.databinding.ActivitySignInBinding
import com.cuervolu.witcherscodex.ui.dashboard.DashboardActivity
import com.cuervolu.witcherscodex.ui.login.LoginActivity
import com.cuervolu.witcherscodex.ui.signin.model.UserSignIn
import com.cuervolu.witcherscodex.ui.verification.VerificationActivity
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Actividad que permite a los usuarios crear una nueva cuenta o registrarse.
 * Proporciona campos para ingresar información como nombre real, nombre de usuario, correo electrónico y contraseña.
 *
 * @property binding El enlace de datos para la actividad.
 * @property signInViewModel El ViewModel asociado a esta actividad.
 * @property dialogLauncher El lanzador de fragmentos de diálogo utilizado para mostrar diálogos en la actividad.
 */
@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private val REQ_ONE_TAP = 1
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest


    companion object {
        /**
         * Crea un intent para iniciar esta actividad.
         *
         * @param context El contexto desde el cual se crea el intent.
         * @return El intent para iniciar la actividad.
         */
        fun create(context: Context): Intent =
            Intent(context, SignInActivity::class.java)
    }

    private val _navigateToDetails = MutableLiveData<Event<Boolean>>()
    val navigateToDetails: LiveData<Event<Boolean>>
        get() = _navigateToDetails
    private lateinit var binding: ActivitySignInBinding
    private val signInViewModel: SignInViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    @Inject
    lateinit var googleClient: GoogleClient

    @Inject
    lateinit var firebaseClient: FirebaseClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
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
                                            signInViewModel.saveUserDataToFirestore(user)
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
            getString(R.string.signin_footer_unselected),
            getString(R.string.signin_footer_selected)
        )
    }

    private fun initListeners() {
        // Configurar listeners para los campos de entrada y botones.
        binding.etEmail.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etEmail.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etEmail.onTextChanged { onFieldChanged() }

        binding.etNickname.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etNickname.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etNickname.onTextChanged { onFieldChanged() }

        binding.etRealName.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etRealName.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etRealName.onTextChanged { onFieldChanged() }

        binding.etPassword.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etPassword.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etPassword.onTextChanged { onFieldChanged() }

        binding.etRepeatPassword.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.etRepeatPassword.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etRepeatPassword.onTextChanged { onFieldChanged() }

        binding.viewBottom.tvFooter.setOnClickListener { signInViewModel.onLoginSelected() }

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


        with(binding) {
            btnCreateAccount.setOnClickListener {
                it.dismissKeyboard()
                signInViewModel.onSignInSelected(
                    UserSignIn(
                        realname = binding.etRealName.text.toString(),
                        nickname = binding.etNickname.text.toString(),
                        email = binding.etEmail.text.toString(),
                        password = binding.etPassword.text.toString(),
                        passwordConfirmation = binding.etRepeatPassword.text.toString()
                    )
                )
            }
        }
    }

    private fun initObservers() {
        // Configurar observadores para eventos en el ViewModel.
        signInViewModel.navigateToVerifyEmail.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerifyEmail()
            }
        }

        signInViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }

        lifecycleScope.launchWhenStarted {
            signInViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        signInViewModel.showErrorDialog.observe(this) { showError ->
            if (showError) showErrorDialog()
        }
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = getString(R.string.signin_error_dialog_body),
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun updateUI(viewState: SignInViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            binding.tilEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.signin_error_mail)
            binding.tilNickname.error =
                if (viewState.isValidNickName) null else getString(R.string.signin_error_nickname)
            binding.tilRealName.error =
                if (viewState.isValidRealName) null else getString(R.string.signin_error_realname)
            binding.tilPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
            binding.tilRepeatPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
        }
    }

    private fun goToDashboard() {
        startActivity(DashboardActivity.create(this))
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            signInViewModel.onFieldsChanged(
                UserSignIn(
                    realname = binding.etRealName.text.toString(),
                    nickname = binding.etNickname.text.toString(),
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    passwordConfirmation = binding.etRepeatPassword.text.toString()
                )
            )
        }
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

    private fun goToVerifyEmail() {
        startActivity(VerificationActivity.create(this))
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }
}