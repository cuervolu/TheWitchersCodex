package com.cuervolu.witcherscodex.data.network

import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.cuervolu.witcherscodex.domain.models.Character
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BestiaryService @Inject constructor(
    private val firebase: FirebaseClient
) {

    // Método para crear una nueva entrada del bestiario
    fun createBestiaryEntry(entry: Bestiary) {
        // Aquí puedes implementar la lógica para agregar el Bestiary a la fuente de datos
    }

    fun getBestiaryEntries(
        lastDocument: DocumentSnapshot?,
        pageSize: Int,
        onSuccess: (List<Bestiary>, DocumentSnapshot?) -> Unit,
        onError: () -> Unit
    ) {
        // Construye la consulta
        val query = firebase.db.collection("bestiary")
            .orderBy("name")
            .limit(pageSize.toLong())

        // Si hay un último documento, comienza desde ese documento
        if (lastDocument != null) {
            Timber.d("LastDocument isn't null")
            query.startAfter(lastDocument)
        } else {
            Timber.w("LastDocument is null")
        }

        // Ejecuta la consulta
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val monsters = mutableListOf<Bestiary>()
                for (document in querySnapshot) {
                    val monster = document.toObject(Bestiary::class.java)
                    monsters.add(monster)
                }

                // Obtén el último documento de la consulta actual
                val newLastDocument = querySnapshot.documents.lastOrNull()
                Timber.d("LastDocument is: ${newLastDocument?.getString("name")}")

                // Llama a la función onSuccess y pasa la lista de monstruos y el último documento
                onSuccess(monsters, newLastDocument)
            }
            .addOnFailureListener { exception ->
                Timber.e("Ha ocurrido un error al cargar los monstruos: ${exception.localizedMessage}")
                onError()
            }
    }

    fun getLastFiveBestiaryEntries(onSuccess: (List<Bestiary>) -> Unit, onError: (String) -> Unit) {
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


    // Método para leer una entrada por su ID
    fun readBestiaryEntry(entryId: String): Bestiary? {
        // Aquí puedes implementar la lógica para obtener una entrada por su ID
        return null // Por defecto, se retorna null si el bestiario no se encuentra
    }

    // Método para actualizar una entrada existente
    fun updateBestiaryEntry(entry: Bestiary) {
        // Aquí puedes implementar la lógica para actualizar una entrada existente
    }

    // Método para eliminar una bestia por su ID
    fun deleteBestiaryEntry(entryId: String) {
        // Aquí puedes implementar la lógica para eliminar una bestia por su ID
    }
}