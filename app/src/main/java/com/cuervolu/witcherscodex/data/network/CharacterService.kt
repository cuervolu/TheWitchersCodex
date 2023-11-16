package com.cuervolu.witcherscodex.data.network

import android.net.Uri
import com.cuervolu.witcherscodex.domain.models.Character
import com.google.firebase.firestore.SetOptions

import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
        TODO("Not yet implemented")
    }

    override fun readEntry(entryId: String, onSuccess: (Character) -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateEntry(entry: Character, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteEntry(entryId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }
}
