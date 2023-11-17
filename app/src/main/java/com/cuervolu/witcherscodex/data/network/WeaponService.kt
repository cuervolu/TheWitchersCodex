package com.cuervolu.witcherscodex.data.network

import android.net.Uri
import com.cuervolu.witcherscodex.domain.models.Weapon
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeaponService @Inject constructor(
    private val firebase: FirebaseClient
) : CRUDService<Weapon> {
    private val weaponCollection = firebase.db.collection("weapons")

    // List (Obtener todos los personajes)
    fun getAllWeapons(onSuccess: (List<Weapon>) -> Unit, onError: () -> Unit) {
        weaponCollection.get().addOnSuccessListener { querySnapshot ->
            val weapons = mutableListOf<Weapon>()
            for (document in querySnapshot) {
                val weapon = document.toObject(Weapon::class.java)
                weapon.entryId = document.id
                weapons.add(weapon)
            }
            onSuccess(weapons)
        }.addOnFailureListener { exception ->
            Timber.e("Ha ocurrido un error al cargr los personajes: ${exception.localizedMessage}")
            onError()
        }
    }

    override fun createEntry(
        entry: Weapon,
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
                    entry.imageUrl = downloadUrl
                    entry.author = currentUser.uid
                    // Crea la entrada en Firestore
                    weaponCollection
                        .add(entry)
                        .addOnSuccessListener { documentReference ->
                            Timber.d("Entrada de Armas creada con ID: ${documentReference.id}")
                            entry.entryId = documentReference.id
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            Timber.e("Error al crear la entrada de armas: ${exception.localizedMessage} | ${exception.cause}")
                            onError()
                        }
                },
                onError = {
                    onError()
                }
            )
        }
    }

    override fun readEntry(entryId: String, onSuccess: (Weapon) -> Unit, onError: () -> Unit) {
        val entryReference = weaponCollection.document(entryId)

        entryReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val entry = documentSnapshot.toObject(Weapon::class.java)
                    entry?.entryId = documentSnapshot.id
                    entry?.let { onSuccess(it) }
                } else {
                    onError()
                }
            }
            .addOnFailureListener { exception ->
                Timber.e("Error al leer la entrada de las armas: ${exception.localizedMessage} | ${exception.cause}")
                onError()
            }
    }

    override fun updateEntry(
        entry: Weapon,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        // Asegúrate de que la entrada tenga un ID válido
        val entryId = entry.entryId
        if (entryId.isBlank()) {
            Timber.w("ID de la entrada del bestiario en blanco.")
            onError()
            return
        }

        if (imageUri != null && entry.imageUrl != imageUri.toString() && imageUri.path?.let {
                File(
                    it
                ).exists()
            } == true) {
            // Si hay una nueva imagen, sube la imagen y actualiza los datos
            uploadImage(
                imageUri,
                onSuccess = { downloadUrl ->
                    // Solo actualiza si la nueva URL es diferente de la existente
                    if (entry.imageUrl != downloadUrl) {
                        entry.imageUrl = downloadUrl
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
        val entryRef = weaponCollection.document(entryId)

        entryRef.delete()
            .addOnSuccessListener {
                Timber.d("Entrada de armas eliminada con ID: $entryId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Timber.e("Error al eliminar la entrada de armas: ${exception.localizedMessage} | ${exception.cause}")
                onError()
            }
    }


    private fun updateEntryInFirestore(
        entry: Weapon,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {
        // Obtiene la versión actual del objeto almacenada en Firestore
        val entryReference = weaponCollection.document(entry.entryId)

        entryReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val originalEntry = documentSnapshot.toObject(Weapon::class.java)

                    // Compara cada campo y actualiza solo si es diferente
                    val updates = mutableMapOf<String, Any>()

                    if (originalEntry?.name != entry.name) {
                        updates["name"] = entry.name
                    }

                    if (originalEntry?.base_damage != entry.base_damage) {
                        updates["base_damage"] = entry.base_damage
                    }

                    if (originalEntry?.bonuses != entry.bonuses) {
                        updates["bonuses"] =
                            entry.bonuses ?: "" // Usa una cadena vacía si entry.bonuses es nulo
                    }

                    if (originalEntry?.crafting_req != entry.crafting_req) {
                        updates["crafting_req"] = entry.crafting_req
                            ?: "" // Usa una cadena vacía si entry.crafting_req es nulo
                    }

                    if (originalEntry?.imageUrl != entry.imageUrl && entry.imageUrl.isNotEmpty()) {
                        updates["imageUrl"] = entry.imageUrl
                    }
                    // Realiza la actualización en Firestore solo si hay cambios
                    if (updates.isNotEmpty()) {
                        weaponCollection
                            .document(entry.entryId)
                            .update(updates)
                            .addOnSuccessListener {
                                Timber.d("Entrada de armas actualizada con ID: ${entry.entryId}")
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                Timber.e("Error al actualizar la entrada de armas: ${exception.localizedMessage} | ${exception.cause}")
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
        val storageRef = firebase.storage.reference.child("images/weapons/${UUID.randomUUID()}")
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