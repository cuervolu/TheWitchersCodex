package com.cuervolu.witcherscodex.data.network

import android.net.Uri
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import timber.log.Timber

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BestiaryService @Inject constructor(
    private val firebase: FirebaseClient
): CRUDService<Bestiary> {


    override fun createEntry(entry: Bestiary, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun readEntry(entryId: String, onSuccess: (Bestiary) -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateEntry(entry: Bestiary, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteEntry(entryId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
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
                    bestiary.add(entry)
                }
                // Invierte la lista para obtener los últimos cinco elementos
                val lastFiveEntries = bestiary.reversed()
                onSuccess(lastFiveEntries)
            }
            .addOnFailureListener { exception ->
                Timber.e("Ha ocurrido un error al cargar las últimas entradas del bestiario: $exception")
                // Llama a la función onError con un mensaje de error
                onError("Ha ocurrido un error al cargar las últimas entradas del bestiario.")
            }
    }


}
