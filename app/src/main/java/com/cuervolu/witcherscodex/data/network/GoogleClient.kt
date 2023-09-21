package com.cuervolu.witcherscodex.data.network

import android.content.Context
import com.cuervolu.witcherscodex.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cliente para interactuar con los servicios de Google Sign-In y One Tap.
 *
 * @property context El contexto de la aplicación.
 */
@Singleton
class GoogleClient @Inject constructor(private val context: Context) {

    /**
     * Obtiene una instancia de [GoogleSignInClient] con las opciones de inicio de sesión configuradas.
     */
    val client: GoogleSignInClient by lazy {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.client_id))
                .requestEmail()
                .build()

        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    /**
     * Obtiene una instancia de [SignInClient] para interactuar con los servicios de One Tap.
     */
    val oneTapClient: SignInClient by lazy {
        Identity.getSignInClient(context)
    }

    /**
     * Obtiene una instancia de [BeginSignInRequest] con la configuración de One Tap.
     */
    val signUpRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }
}
