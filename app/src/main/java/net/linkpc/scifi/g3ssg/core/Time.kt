package net.linkpc.scifi.g3ssg.core

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import kotlin.reflect.KClass

sealed class Time(internal val raw:Double) {
    class Days(n:Double) : Time(n) {
        constructor(n:Int) : this(n.toDouble())
        fun toHours() = this.convert<Hours>()
    }
    class Hours(n:Double) : Time(n) {
        constructor(n:Int) : this(n.toDouble())
        fun toDays() = this.convert<Days>()
    }

    companion object {
        init {
            adapters.add(AdapterD())
            adapters.add(AdapterH())
        }
    }

    fun toDouble() = raw

    class AdapterD : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean = from is Days && to == Hours::class
        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as Days
            return Hours(a.raw * 24) as T
        }
    }

    class AdapterH : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean = from is Hours && to == Days::class
        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as Hours
            return Days(a.raw / 24) as T
        }
    }
}


val Number.d get() = Time.Days(this.toDouble())
val Number.h get() = Time.Hours(this.toDouble())
