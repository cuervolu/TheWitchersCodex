package com.cuervolu.witcherscodex.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cliente para interactuar con los servicios de Firebase, incluyendo la autenticación y Firestore.
 *
 * @property auth Instancia de [FirebaseAuth] para gestionar la autenticación de usuarios.
 * @property db Instancia de Firestore [FirebaseFirestore] para interactuar con la base de datos en
 * tiempo real de Firebase.
 */
@Singleton
class FirebaseClient @Inject constructor() {

    /**
     * Obtiene una instancia de [FirebaseAuth] para gestionar la autenticación de usuarios.
     */
    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    /**
     * Instancia de Firestore [FirebaseFirestore] para interactuar con la base de datos en tiempo
     * real de Firebase.
     */
    val db = Firebase.firestore
}
