package com.cuervolu.witcherscodex.data.network

import android.net.Uri
import com.cuervolu.witcherscodex.domain.models.Character
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterService @Inject constructor(
    private val firebase: FirebaseClient
) : CRUDService<Character> {
    private val characterCollection = firebase.db.collection("characters")

    // List (Obtener todos los personajes)
    fun getAllCharacters(onSuccess: (List<Character>) -> Unit, onError: () -> Unit) {
        characterCollection.get().addOnSuccessListener { querySnapshot ->
            val characters = mutableListOf<Character>()
            for (document in querySnapshot) {
                val character = document.toObject(Character::class.java)
                character.entryId = document.id
                characters.add(character)
            }
            onSuccess(characters)
        }.addOnFailureListener { exception ->
            Timber.e("Ha ocurrido un error al cargr los personajes: ${exception.localizedMessage}")
            onError()
        }
    }

    override fun createEntry(
        entry: Character,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val currentUser = firebase.auth.currentUser
        if (currentUser == null) {
            onError()
            return
        }
        // Sube la imagen a Firebase Storage y obtén la URL de descarga
        if (imageUri != null) {
            uploadImage(imageUri,
                onSuccess = { downloadUrl ->
                    // Añade la URL de la imagen al objeto Bestiary
                    entry.image_url = downloadUrl
                    entry.author = currentUser.uid
                    // Crea la entrada en Firestore
                    firebase.db.collection("characters")
                        .add(entry)
                        .addOnSuccessListener { documentReference ->
                            Timber.d("Entrada del personaje creada con ID: ${documentReference.id}")
                            entry.entryId = documentReference.id
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            Timber.e("Error al crear la entrada del personaje: ${exception.localizedMessage} | ${exception.cause}")
                            onError()
                        }
                },
                onError = {
                    onError()
                }
            )
        }
    }

    override fun readEntry(entryId: String, onSuccess: (Character) -> Unit, onError: () -> Unit) {
        val entryReference = firebase.db.collection("characters").document(entryId)

        entryReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val entry = documentSnapshot.toObject(Character::class.java)
                    entry?.entryId = documentSnapshot.id
                    entry?.let { onSuccess(it) }
                } else {
                    onError()
                }
            }
            .addOnFailureListener { exception ->
                Timber.e("Error al leer la entrada del personaje: ${exception.localizedMessage} | ${exception.cause}")
                onError()
            }
    }

    override fun updateEntry(
        entry: Character,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        // Asegúrate de que la entrada tenga un ID válido
        val entryId = entry.entryId
        if (entryId.isBlank()) {
            Timber.w("ID de la entrada del personaje en blanco.")
            onError()
            return
        }

        if (imageUri != null && entry.image_url != imageUri.toString() && imageUri.path?.let {
                File(
                    it
                ).exists()
            } == true) {
            // Si hay una nueva imagen, sube la imagen y actualiza los datos
            uploadImage(
                imageUri,
                onSuccess = { downloadUrl ->
                    // Solo actualiza si la nueva URL es diferente de la existente
                    if (entry.image_url != downloadUrl) {
                        entry.image_url = downloadUrl
                    }
                    // Actualiza los datos en Firestore
                    updateEntryInFirestore(entry, onSuccess, onError)
                },
                onError = {
                    onError()
                }
            )
        } else {
            // Si no hay nueva imagen, solo actualiza los datos en Firestore
            updateEntryInFirestore(entry, onSuccess, onError)
        }
    }

    override fun deleteEntry(entryId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val entryRef = firebase.db.collection("characters").document(entryId)

        entryRef.delete()
            .addOnSuccessListener {
                Timber.d("Entrada del personaje eliminada con ID: $entryId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Timber.e("Error al eliminar la entrada del personaje: ${exception.localizedMessage} | ${exception.cause}")
                onError()
            }
    }


    private fun updateEntryInFirestore(
        entry: Character,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {
        // Obtiene la versión actual del objeto almacenada en Firestore
        val entryReference = firebase.db.collection("characters").document(entry.entryId)

        entryReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val originalEntry = documentSnapshot.toObject(Character::class.java)

                    // Compara cada campo y actualiza solo si es diferente
                    val updates = mutableMapOf<String, Any>()

                    if (originalEntry?.name != entry.name) {
                        updates["name"] = entry.name
                    }

                    if (originalEntry?.description != entry.description) {
                        updates["description"] = entry.description
                    }

                    if (originalEntry?.alias != entry.alias) {
                        updates["alias"] = entry.alias
                    }

                    if (originalEntry?.age != entry.age) {
                        updates["age"] = entry.age ?: ""
                    }

                    if (originalEntry?.basic_information != entry.basic_information) {
                        updates["basic_information"] = entry.basic_information
                            ?: "" // Usa una cadena vacía si entry.loot es nulo
                    }

                    if (originalEntry?.image_url != entry.image_url && entry.image_url.isNotEmpty()) {
                        updates["image_url"] = entry.image_url
                    }

                    // Realiza la actualización en Firestore solo si hay cambios
                    if (updates.isNotEmpty()) {
                        firebase.db.collection("characters")
                            .document(entry.entryId)
                            .update(updates)
                            .addOnSuccessListener {
                                Timber.d("Entrada del characters actualizada con ID: ${entry.entryId}")
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                Timber.e("Error al actualizar la entrada del characters: ${exception.localizedMessage} | ${exception.cause}")
                                onError()
                            }
                    } else {
                        // No hay cambios, llama directamente al éxito
                        onSuccess()
                    }
                } else {
                    onError()
                }
            }
            .addOnFailureListener { exception ->
                Timber.e("Error al leer la entrada del bestiario para comparar: ${exception.localizedMessage} | ${exception.cause}")
                onError()
            }
    }


    private fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onError: () -> Unit) {
        val storageRef = firebase.storage.reference.child("images/characters/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Timber.e("Error al subir la imagen: ${it.localizedMessage} | ${it.cause}")
                    throw it.localizedMessage?.let { it1 -> Exception(it1) }!!
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onSuccess(downloadUri.toString())
            } else {
                onError()
            }
        }
    }
}
