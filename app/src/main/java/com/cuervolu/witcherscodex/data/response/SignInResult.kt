package com.cuervolu.witcherscodex.data.response

import android.net.Uri

sealed class SignInResult {
    data class Success(
        val googleSignInRequest: String?,
        val displayName: String?,
        val photoUrl: Uri?
    ) : SignInResult()
    data class Error(val errorMessage: String) : SignInResult()
}
