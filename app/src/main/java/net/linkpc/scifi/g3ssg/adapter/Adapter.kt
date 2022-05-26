package net.linkpc.scifi.g3ssg.adapter

import java.lang.Exception
import kotlin.reflect.KClass

interface Adapter {
    fun <T: Any> canAdapt(from: Any, to: KClass<T>): Boolean
    fun <T: Any> adaptTo(from: Any, to: KClass<T>): T
}

val adapters = mutableListOf<Adapter>()

inline fun <reified T : Any> Any.adaptTo(): T {
    val adapter = adapters.find { it.canAdapt(this, T::class) }
        ?: throw NoSuitableAdapterFoundException(this, T::class)
    return adapter.adaptTo(this, T::class)
}

class NoSuitableAdapterFoundException(from: Any, to: KClass<*>)
    : Exception("Adapter not found/registered to convert $from to $to")
