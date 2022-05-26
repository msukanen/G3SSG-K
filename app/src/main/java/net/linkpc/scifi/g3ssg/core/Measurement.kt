package net.linkpc.scifi.g3ssg.core

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.NoSuitableAdapterFoundException
import net.linkpc.scifi.g3ssg.adapter.adaptTo
import net.linkpc.scifi.g3ssg.adapter.adapters
import kotlin.reflect.KClass

interface Length {
    fun toDouble(): Double
}

sealed class Distance(guts:Double) : Length {
    protected var raw: Double = guts
        set(value) {
            field = if(value < 0.0) 0.0 else value
        }
    override fun toDouble() = raw
}

class AU(guts:Double) : Distance(guts) {
    class AUtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is AU && (to == AU::class || to == LY::class || to == PC::class)

        override fun <T : Any> adaptTo(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as AU
            return (when (to) {
                AU::class -> a
                LY::class -> LY(a.toDouble() / 63_241.077_088_071)
                PC::class -> adaptTo<LY>().adaptTo<PC>()
                else -> throw NoSuitableAdapterFoundException(from, to::class)
            }) as T
        }
    }

    companion object {
        val Zero: AU = AU(0.0)
        init {
            adapters.add(AUtoXAdapter())
        }
    }
}

class LY(guts:Double) : Distance(guts) {
    class LYtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is LY && (to == AU::class || to == LY::class || to == PC::class)

        override fun <T : Any> adaptTo(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as LY
            return (when(to) {
                AU::class -> AU(a.raw * 63_241.077_088_071)
                LY::class -> a
                PC::class -> PC(a.raw / 3.261_563_776_9)
                else -> throw NoSuitableAdapterFoundException(from, to::class)
            }) as T
        }
    }

    companion object {
        init {
            adapters.add(LYtoXAdapter())
        }
    }
}

class PC(guts: Double):Distance(guts) {
    class PCtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is PC && (to == AU::class || to == LY::class || to == PC::class)

        override fun <T : Any> adaptTo(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as PC
            return (when(to) {
                AU::class -> adaptTo<LY>().adaptTo<AU>()
                LY::class -> PC(a.raw * 3.261_563_776_9)
                PC::class -> a
                else -> throw NoSuitableAdapterFoundException(from, to::class)
            }) as T
        }
    }

    companion object {
        init {
            adapters.add(PCtoXAdapter())
        }
    }
}

val Number.AU get() = AU(this.toDouble())
val Number.ly get() = LY(this.toDouble())
val Number.pc get() = PC(this.toDouble())

sealed class Coord()

sealed class Location(x:Coord, y:Coord, z:Coord)
