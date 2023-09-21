package com.cuervolu.witcherscodex.core

/**
 * Clase genérica que representa un evento único en una arquitectura de aplicaciones. Se utiliza
 * para transmitir datos de un solo uso, como eventos de navegación o mensajes de estado.
 *
 * @param T El tipo de datos contenidos en el evento.
 *
 * @property content El contenido del evento.
 * @property hasBeenHandled Un indicador que rastrea si el evento ha sido manejado o consumido.
 * Una vez que se ha consumido, no se puede volver a consumir.
 */
open class Event<out T>(private val content: T) {

    /**
     * Indica si el evento ha sido manejado o consumido.
     */
    var hasBeenHandled = false
        private set

    /**
     * Obtiene el contenido del evento si aún no ha sido manejado. Una vez que se ha consumido,
     * devuelve `null`.
     *
     * @return El contenido del evento si no ha sido manejado, o `null` si ya ha sido consumido.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Obtiene el contenido del evento, independientemente de si ha sido manejado o no.
     *
     * @return El contenido del evento.
     */
    fun getContent(): T? {
        return content
    }

    /**
     * Obtiene el contenido del evento sin marcarlo como manejado. Esto permite "mirar" el
     * contenido sin consumirlo.
     *
     * @return El contenido del evento.
     */
    fun peekContent(): T = content
}
