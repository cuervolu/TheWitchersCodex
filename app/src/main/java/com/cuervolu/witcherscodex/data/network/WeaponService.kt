package com.cuervolu.witcherscodex.data.network

import com.cuervolu.witcherscodex.domain.models.Character
import com.cuervolu.witcherscodex.domain.models.Weapon
import com.cuervolu.witcherscodex.domain.models.Weapons
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeaponService @Inject constructor(
    private val firebase: FirebaseClient
) {
    private val weaponCollection = firebase.db.collection("weapons")

    // Create (Agregar una nueva arma)
    suspend fun addCharacter(weapon: Weapon) {
        weaponCollection.add(weapon)
    }

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

    // Read (Obtener un arma por su ID)
    suspend fun getWeaponById(weaponId: String): Weapon? {
        val snapshot = weaponCollection.document(weaponId).get().await()
        return snapshot.toObject(Weapon::class.java)
    }

    // Update (Actualizar un arma existente)
    fun updateWeapon(weaponId: String, updatedWeapon: Weapon) {
        weaponCollection.document(weaponId).set(updatedWeapon, SetOptions.merge())
    }

    // Delete (Eliminar un arma por su ID)
    fun deleteWeapon(characterId: String) {
        weaponCollection.document(characterId).delete()
    }
}