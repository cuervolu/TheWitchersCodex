package com.cuervolu.witcherscodex.data.network

import com.cuervolu.witcherscodex.domain.models.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * Servicio para interactuar con la colección de usuarios en Firebase Firestore.
 *
 * @property firebase Cliente de Firebase utilizado para acceder a Firestore.
 */
class UserService @Inject constructor(private val firebase: FirebaseClient) {

    /**
     * Nombre de la colección en Firestore donde se almacenan los datos de los usuarios.
     */
    companion object {
        const val USER_COLLECTION = "users"
    }

    /**
     * Guarda los datos del usuario en Firestore si no existen previamente.
     *
     * @param user Usuario autenticado en Firebase.
     * @param realname Nombre real del usuario (opcional, por defecto null).
     * @param nickname Nombre de usuario (opcional, por defecto null).
     */
    fun saveUserDataToFirestore(
        user: FirebaseUser?, realname: String? = null, nickname: String? = null
    ) {
        // Verifica si el usuario no es nulo
        if (user != null) {
            // Crea una referencia al documento del usuario en la colección "users"
            val userDocument = firebase.db.collection("users").document(user.uid)

            // Verifica si el usuario ya existe en Firestore
            userDocument.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // El usuario ya existe en Firestore, no es necesario guardarlo nuevamente
                        Timber.d("User already exists in Firestore")
                        // Puedes realizar alguna acción adicional aquí si es necesario
                    } else {
                        // El usuario no existe en Firestore, procede a guardarlo
                        // Crea un objeto User con los datos del usuario de Firebase
                        val userData = User(
                            email = user.email,
                            nickname = nickname ?: user.displayName,
                            realname = realname ?: user.displayName,
                            imageUrl = user.photoUrl.toString() // Convierte la URL de la imagen a String
                        )

                        // Intenta guardar el objeto userData en Firestore
                        userDocument.set(userData).addOnSuccessListener {
                                // Los datos del usuario se han guardado con éxito en Firestore
                                Timber.d("User data saved to Firestore")
                            }.addOnFailureListener { e: Exception ->
                                // Maneja cualquier error aquí
                                Timber.e("Error saving user data to Firestore: ${e.localizedMessage}")
                                // También puedes mostrar un mensaje de error al usuario si lo deseas
                            }
                    }
                }.addOnFailureListener { e: Exception ->
                    // Maneja cualquier error aquí
                    Timber.e("Error checking user existence in Firestore: ${e.localizedMessage}")
                    // También puedes mostrar un mensaje de error al usuario si lo deseas
                }
        }
    }

    /**
     * Obtiene los datos del usuario desde Firestore.
     *
     * @return Un objeto User con los datos del usuario o un objeto User vacío en caso de error.
     */
    suspend fun getUserDataFromFirestore(): User {
        val userId = firebase.auth.currentUser?.uid ?: ""
        Timber.d("Loading user: $userId")
        val userDocument = firebase.db.collection(USER_COLLECTION).document(userId).get().await()

        if (userDocument.exists()) {
            val userData = userDocument.toObject(User::class.java)
            userData?.let {
                return it
            }
        }

        // Devuelve un objeto UserData vacío o un valor predeterminado en caso de error
        return User()
    }
}
