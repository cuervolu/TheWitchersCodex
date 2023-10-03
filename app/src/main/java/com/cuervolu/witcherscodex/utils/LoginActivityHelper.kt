package com.cuervolu.witcherscodex.utils

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import com.cuervolu.witcherscodex.data.network.FirebaseClient
import com.cuervolu.witcherscodex.data.network.GoogleClient
import com.cuervolu.witcherscodex.databinding.ActivityLoginBinding
import com.cuervolu.witcherscodex.ui.dashboard.DashboardActivity
import com.cuervolu.witcherscodex.ui.login.LoginViewModel
import com.cuervolu.witcherscodex.ui.login.LoginViewState
import com.cuervolu.witcherscodex.ui.signin.SignInActivity
import com.cuervolu.witcherscodex.ui.verification.VerificationActivity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber
import javax.inject.Inject

class LoginActivityHelper @Inject constructor(
    private val loginViewModel: LoginViewModel,
    private val googleClient: GoogleClient,
    private val firebaseClient: FirebaseClient
) {
    fun handleOneTapResult(resultCode: Int, data: Intent?, context: Context) {
        if (resultCode == RESULT_OK) {
            try {
                val credential = googleClient.oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        signInWithGoogleIdToken(idToken, context)
                    }
                }
            } catch (e: ApiException) {
                Timber.e("An error occurred: ${e.message}")
            }
        } else {
            // Authentication failed, handle the error case
            Timber.d("Authentication failed with result code: $resultCode")
        }
    }

    private fun signInWithGoogleIdToken(idToken: String, context: Context) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseClient.auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")
                    val user = firebaseClient.auth.currentUser
                    loginViewModel.saveUserDataToFirestore(user)
                    goToDashboard(context)
                } else {
                    // If sign-in fails, display a message to the user.
                    Timber.w("signInWithCredential:failure ${task.exception}")
                    // Handle failure action (e.g., show an error dialog)
                }
            }
    }

    fun goToDashboard(context: Context) {
        context.startActivity(DashboardActivity.create(context))
        if (context is Activity) {
            context.finishAffinity()
        }
    }

     fun goToSignIn(context: Context) {
         context.startActivity(SignInActivity.create(context))
         if (context is Activity) {
             context.finishAffinity()
         }
    }

    fun goToVerify(context: Context) {
        context.startActivity(VerificationActivity.create(context))
        if (context is Activity) {
            context.finishAffinity()
        }
    }

    fun updateUI(context: Context,viewState: LoginViewState, binding: ActivityLoginBinding) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            tilEmail.error =
                if (viewState.isValidEmail) null else getString(context,com.cuervolu.witcherscodex.R.string.login_error_mail)
            tilPassword.error =
                if (viewState.isValidPassword) null else getString(context,com.cuervolu.witcherscodex.R.string.login_error_password)
        }
    }

    fun onFieldChanged(email: String, password: String) {
        loginViewModel.onFieldsChanged(email = email, password = password)
    }
}