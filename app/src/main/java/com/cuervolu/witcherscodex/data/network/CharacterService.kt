package com.cuervolu.witcherscodex.data.network

import com.cuervolu.witcherscodex.domain.models.Character
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterService @Inject constructor(
    private val firebase: FirebaseClient
) {
    private val characterCollection = firebase.db.collection("characters")

    // Create (Agregar un nuevo personaje)
    suspend fun addCharacter(character: Character) {
        characterCollection.add(character)
    }

    // List (Obtener todos los personajes)
    fun getAllCharacters(onSuccess: (List<Character>) -> Unit, onError: () -> Unit) {
        characterCollection.get().addOnSuccessListener { querySnapshot ->
            val characters = mutableListOf<Character>()
            for (document in querySnapshot) {
                val character = document.toObject(Character::class.java)
                characters.add(character)
            }
            onSuccess(characters)
        }.addOnFailureListener { exception ->
            Timber.e("Ha ocurrido un error al cargr los personajes: ${exception.localizedMessage}")
            onError()
        }
    }


    // Read (Obtener un personaje por su ID)
    suspend fun getCharacterById(characterId: String): Character? {
        val snapshot = characterCollection.document(characterId).get().await()
        return snapshot.toObject(Character::class.java)
    }

    // Update (Actualizar un personaje existente)
    fun updateCharacter(characterId: String, updatedCharacter: Character) {
        characterCollection.document(characterId).set(updatedCharacter, SetOptions.merge())
    }

    // Delete (Eliminar un personaje por su ID)
    fun deleteCharacter(characterId: String) {
        characterCollection.document(characterId).delete()
    }
}
