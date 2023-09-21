package com.cuervolu.witcherscodex.core.delegate

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * Una propiedad delegada que almacena una referencia débil a un objeto [T]. Esto permite que el objeto referenciado
 * sea liberado por el recolector de basura cuando no es referenciado por ninguna otra parte del código.
 *
 * @param T El tipo de objeto al que se hará referencia débilmente.
 * @constructor Crea una instancia de [WeakReferenceDelegate] sin inicializarla.
 * @constructor Crea una instancia de [WeakReferenceDelegate] con un valor inicial [value].
 */
inline fun <reified T> weak() = WeakReferenceDelegate<T>()

/**
 * Una propiedad delegada que almacena una referencia débil a un objeto [T] con un valor inicial.
 *
 * @param T El tipo de objeto al que se hará referencia débilmente.
 * @param value El valor inicial que se almacenará en la referencia débil.
 */
inline fun <reified T> weak(value: T) = WeakReferenceDelegate(value)

/**
 * Clase que implementa la propiedad delegada para almacenar una referencia débil a un objeto [T].
 *
 * @param T El tipo de objeto al que se hará referencia débilmente.
 *
 * @property weakReference La referencia débil al objeto [T].
 */
class WeakReferenceDelegate<T> {
    private var weakReference: WeakReference<T>? = null

    constructor()
    constructor(value: T) {
        weakReference = WeakReference(value)
    }

    /**
     * Obtiene el valor almacenado en la referencia débil. Si el objeto referenciado ha sido liberado por el
     * recolector de basura, devuelve `null`.
     *
     * @param thisRef El objeto que contiene esta propiedad delegada.
     * @param property La descripción de la propiedad delegada.
     * @return El objeto [T] almacenado en la referencia débil o `null` si ha sido liberado.
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T? = weakReference?.get()

    /**
     * Establece el valor almacenado en la referencia débil. Si se proporciona un valor no nulo, se crea una nueva
     * referencia débil para ese valor.
     *
     * @param thisRef El objeto que contiene esta propiedad delegada.
     * @param property La descripción de la propiedad delegada.
     * @param value El valor que se almacenará en la referencia débil.
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        weakReference = value?.let(::WeakReference)
    }
}
