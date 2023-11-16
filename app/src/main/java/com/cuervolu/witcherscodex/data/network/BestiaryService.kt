package com.cuervolu.witcherscodex.data.network

import android.net.Uri
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.google.firebase.firestore.Query
import timber.log.Timber
import java.util.UUID

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BestiaryService @Inject constructor(
    private val firebase: FirebaseClient
) : CRUDService<Bestiary> {


    override fun createEntry(
        entry: Bestiary,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val currentUser = firebase.auth.currentUser
        if(currentUser == null) {
            onError()
            return
        }
        // Sube la imagen a Firebase Storage y obtén la URL de descarga
        if (imageUri != null) {
            uploadImage(imageUri,
                onSuccess = { downloadUrl ->
                    // Añade la URL de la imagen al objeto Bestiary
                    entry.image = downloadUrl

                    // Crea la entrada en Firestore
                    firebase.db.collection("bestiary")
                        .add(entry)
                        .addOnSuccessListener { documentReference ->
                            Timber.d("Entrada del bestiario creada con ID: ${documentReference.id}")
                            entry.entryId = documentReference.id
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            Timber.e("Error al crear la entrada del bestiario: ${exception.localizedMessage} | ${exception.cause}")
                            onError()
                        }
                },
                onError = {
                    onError()
                }
            )
        }
    }

    override fun readEntry(entryId: String, onSuccess: (Bestiary) -> Unit, onError: () -> Unit) {
        val entryReference = firebase.db.collection("bestiary").document(entryId)

        entryReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val entry = documentSnapshot.toObject(Bestiary::class.java)
                    entry?.entryId = documentSnapshot.id
                    entry?.let { onSuccess(it) }
                } else {
                    onError()
                }
            }
            .addOnFailureListener { exception ->
                Timber.e("Error al leer la entrada del bestiario: ${exception.localizedMessage} | ${exception.cause}")
                onError()
            }
    }


    override fun updateEntry(entry: Bestiary, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteEntry(entryId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    private fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onError: () -> Unit) {
        val storageRef = firebase.storage.reference.child("images/bestiary/${UUID.randomUUID()}")
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

    fun getLastFiveBestiaryEntries(
        onSuccess: (List<Bestiary>) -> Unit,
        onError: (String) -> Unit
    ) {
        // Accede a la colección "bestiary" en Firebase Firestore
        val query = firebase.db.collection("bestiary")
            .orderBy(
                "name",
                Query.Direction.DESCENDING
            ) // Ordena según el nombre de forma descendente
            .limit(5) // Limita la consulta a los últimos cinco elementos

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val bestiary = mutableListOf<Bestiary>()
                for (document in querySnapshot) {
                    // Convierte los documentos de Firestore en objetos de tipo Bestiary
                    val entry = document.toObject(Bestiary::class.java)
                    entry.entryId = document.id
                    bestiary.add(entry)
                }
                if (bestiary.isNotEmpty()) {
                    // Invierte la lista para obtener los últimos cinco elementos
                    val lastFiveEntries = bestiary.reversed()
                    onSuccess(lastFiveEntries)
                } else {
                    onError("No hay entradas en el bestiario.")
                }
            }
            .addOnFailureListener { exception ->
                Timber.e("Ha ocurrido un error al cargar las últimas entradas del bestiario: $exception")
                // Llama a la función onError con un mensaje de error
                onError("Ha ocurrido un error al cargar las últimas entradas del bestiario.")
            }

    }


}
