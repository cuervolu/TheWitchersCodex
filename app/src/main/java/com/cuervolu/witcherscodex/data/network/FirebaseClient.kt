package com.cuervolu.witcherscodex.data.network

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cliente para interactuar con los servicios de Firebase, incluyendo la autenticación y Firestore.
 *
 * @property auth Instancia de [FirebaseAuth] para gestionar la autenticación de usuarios.
 * @property db Instancia de Firestore [FirebaseFirestore] para interactuar con la base de datos en
 * tiempo real de Firebase.
 * @property storage Instancia de [FirebaseStorage] para interactuar con el servicio de almacenamiento
 * de Firebase.
 * @property appCheck Instancia de [FirebaseAppCheck] para interactuar con el servicio de seguridad
 * de Firebase.
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

    val storage = FirebaseStorage.getInstance()

    val appCheck = FirebaseAppCheck.getInstance()
}
