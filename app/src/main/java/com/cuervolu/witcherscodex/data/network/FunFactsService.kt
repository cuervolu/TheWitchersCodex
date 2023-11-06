package com.cuervolu.witcherscodex.data.network

import android.content.Context
import android.net.Uri
import com.cuervolu.witcherscodex.domain.models.FunFact
import com.google.android.gms.tasks.Tasks
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FunFactsService @Inject constructor(
    private val firebase: FirebaseClient,
    val context: Context
) {
    private val db = firebase.db
    private val storage = firebase.storage


    fun createFunFact(entry: FunFact, onSuccess: () -> Unit, onError: () -> Unit) {
        db.runTransaction { transaction ->
            val funFactsRef = db.collection("funFacts")
            val newFunFactDoc = funFactsRef.document()

            // Agregar el documento a Firestore en la transacción
            transaction.set(newFunFactDoc, entry)

            // Subir la imagen a Firebase Storage
            val entryId = newFunFactDoc.id
            val imageUri = Uri.parse(entry.imageUrl)
            val storageRef = storage.reference.child("/images/funFacts/$entryId")

            try {
                val uploadTask = storageRef.putFile(imageUri)
                val storageTask = Tasks.await(uploadTask)

                if (storageTask.task.isSuccessful) {
                    // La imagen se ha subido con éxito. Obten la URL de descarga.
                    val imageUrl = storageRef.downloadUrl.result.toString()

                    // Actualiza el documento recién creado con la URL de la imagen.
                    val dataToUpdate = hashMapOf("imageUrl" to imageUrl)
                    newFunFactDoc.update(dataToUpdate as Map<String, Any>)

                    onSuccess()
                } else {
                    // Ocurrió un error al subir la imagen.
                    onError()
                }
            } catch (e: Exception) {
                // Manejar errores en la transacción
                Timber.e("Ha ocurrido un error en la transacción: ${e.localizedMessage}")
                onError()
            }

            // Retorna un resultado arbitrario, ya que la transacción es exitosa
            null
        }.addOnSuccessListener {
            // La transacción fue exitosa (documento creado y imagen subida).
        }.addOnFailureListener { e ->
            // Ocurrió un error en la transacción.
            Timber.e("Ha ocurrido un error en la transacción: ${e.localizedMessage}")
            onError()
        }
    }
}