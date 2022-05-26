package net.linkpc.scifi.g3ssg.core

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.adapters
import kotlin.reflect.KClass

sealed class Time(val raw:Double) {
    class Days(n:Double) : Time(n) {
        constructor(n:Int) : this(n.toDouble())
    }
    class Hours(n:Double) : Time(n) {
        constructor(n:Int) : this(n.toDouble())
    }

    companion object {
        init {
            adapters.add(TimeAdapterD())
            adapters.add(TimeAdapterH())
        }
    }
}

class TimeAdapterD : Adapter {
    override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean = from is Time.Days && to == Time.Hours::class
    override fun <T : Any> adaptTo(from: Any, to: KClass<T>): T {
        require(canAdapt(from, to))
        val a = from as Time.Days
        return Time.Hours(a.raw * 24) as T
    }
}

class TimeAdapterH : Adapter {
    override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean = from is Time.Hours && to == Time.Days::class
    override fun <T : Any> adaptTo(from: Any, to: KClass<T>): T {
        require(canAdapt(from, to))
        val a = from as Time.Hours
        return Time.Days(a.raw / 24) as T
    }
}

val Number.d get() = Time.Days(this.toDouble())
val Number.h get() = Time.Hours(this.toDouble())
