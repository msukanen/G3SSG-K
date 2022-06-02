package net.linkpc.scifi.g3ssg.unit.distance

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.NoSuitableAdapterFoundException
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.unit.Distance
import kotlin.reflect.KClass

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

val Number.ly get() = LY(this.toDouble())
