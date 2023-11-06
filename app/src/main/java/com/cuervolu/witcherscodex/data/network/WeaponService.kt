package com.cuervolu.witcherscodex.data.network

import com.cuervolu.witcherscodex.domain.models.Weapon
import com.google.firebase.firestore.SetOptions

import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeaponService @Inject constructor(
    firebase: FirebaseClient
): CRUDService<Weapon> {
    private val weaponCollection = firebase.db.collection("weapons")

    // List (Obtener todos los personajes)
    fun getAllWeapons(onSuccess: (List<Weapon>) -> Unit, onError: () -> Unit) {
        weaponCollection.get().addOnSuccessListener { querySnapshot ->
            val weapons = mutableListOf<Weapon>()
            for (document in querySnapshot) {
                val weapon = document.toObject(Weapon::class.java)
                weapons.add(weapon)
            }
            onSuccess(weapons)
        }.addOnFailureListener { exception ->
            Timber.e("Ha ocurrido un error al cargr los personajes: ${exception.localizedMessage}")
            onError()
        }
    }
    override fun createEntry(entry: Weapon, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun readEntry(entryId: String, onSuccess: (Weapon) -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateEntry(entry: Weapon, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteEntry(entryId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }
}