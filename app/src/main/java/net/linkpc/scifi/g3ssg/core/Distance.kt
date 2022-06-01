package net.linkpc.scifi.g3ssg.core

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.NoSuitableAdapterFoundException
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import kotlin.reflect.KClass

interface Distance {
    fun toDouble(): Double
}

sealed class CoreDistance(guts:Double) : Distance, Comparable<Distance> {
    protected var raw: Double = guts
        set(value) {
            field = if(value < 0.0) 0.0 else value
        }
    override fun toDouble() = raw
}

class AU(guts:Double): CoreDistance(guts) {
    class AUtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is AU && (to == AU::class || to == LY::class || to == PC::class)

        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as AU
            return (when (to) {
                AU::class -> a
                LY::class -> LY(a.toDouble() / 63_241.077_088_071)
                PC::class -> convert<LY>().convert<PC>()
                else -> throw NoSuitableAdapterFoundException(from, to::class)
            }) as T
        }
    }

    operator fun times(other: Number): AU = AU(raw * other.toDouble())
    override fun compareTo(other: Distance): Int =
        toDouble().compareTo(other.convert<AU>().toDouble())

    companion object {
        val Zero: AU = AU(0.0)

        init {
            adapters.add(AUtoXAdapter())
        }
    }
}

class LY(guts:Double): CoreDistance(guts) {
    class LYtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is LY && (to == AU::class || to == LY::class || to == PC::class)

        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as LY
            return (when (to) {
                AU::class -> AU(a.raw * 63_241.077_088_071)
                LY::class -> a
                PC::class -> PC(a.raw / 3.261_563_776_9)
                else -> throw NoSuitableAdapterFoundException(from, to::class)
            }) as T
        }
    }

    override fun compareTo(other: Distance): Int =
        toDouble().compareTo(other.convert<LY>().toDouble())

    companion object {
        init {
            adapters.add(LYtoXAdapter())
        }
    }
}

class PC(guts: Double): CoreDistance(guts) {
    class PCtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is PC && (to == AU::class || to == LY::class || to == PC::class)

        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as PC
            return (when (to) {
                AU::class -> convert<LY>().convert<AU>()
                LY::class -> PC(a.raw * 3.261_563_776_9)
                PC::class -> a
                else -> throw NoSuitableAdapterFoundException(from, to::class)
            }) as T
        }
    }

    override fun compareTo(other: Distance): Int =
        toDouble().compareTo(other.convert<PC>().toDouble())

    companion object {
        init {
            adapters.add(PCtoXAdapter())
        }
    }
}

val Number.AU get() = AU(this.toDouble())
val Number.ly get() = LY(this.toDouble())
val Number.pc get() = PC(this.toDouble())
