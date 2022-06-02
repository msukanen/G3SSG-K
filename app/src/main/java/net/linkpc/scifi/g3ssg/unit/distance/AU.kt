package net.linkpc.scifi.g3ssg.unit.distance

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.NoSuitableAdapterFoundException
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.unit.Distance
import kotlin.reflect.KClass

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

val Number.AU get() = AU(this.toDouble())
