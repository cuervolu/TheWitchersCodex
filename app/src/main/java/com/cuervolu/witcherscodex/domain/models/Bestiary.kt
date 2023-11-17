package com.cuervolu.witcherscodex.domain.models

import android.os.Parcel
import android.os.Parcelable

data class Bestiary(
    var entryId: String = "", // ID de la entrada en el bestiario
    val name: String = "", // Nombre del ser
    val type: String = "", // Tipo del ser (por ejemplo, "Relicts")
    val location: String = "", // Ubicación donde se encuentra el ser
    val desc: String = "", // Descripción del ser
    var image: String = "", // URL de la imagen asociada al ser
    val loot: String? = "", // Botín que se puede obtener del ser (puede ser nulo)
    val weakness: String? = "", // Debilidad del ser (puede ser nulo)
    var author: String? = "" // Autor de la entrada (puede ser nulo)
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        // Write data in any order
        dest.writeString(name)
        dest.writeString(type)
        dest.writeString(location)
        dest.writeString(desc)
        dest.writeString(image)
        dest.writeString(loot)
        dest.writeString(weakness)
        dest.writeString(author)
    }

    companion object CREATOR : Parcelable.Creator<Bestiary> {
        override fun createFromParcel(parcel: Parcel): Bestiary {
            return Bestiary(parcel)
        }

        override fun newArray(size: Int): Array<Bestiary?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bestiary

        if (entryId != other.entryId) return false

        return true
    }

    override fun hashCode(): Int {
        return entryId.hashCode()
    }
}
