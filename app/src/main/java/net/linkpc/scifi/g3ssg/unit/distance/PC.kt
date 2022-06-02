package net.linkpc.scifi.g3ssg.unit.distance

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.NoSuitableAdapterFoundException
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.unit.Distance
import kotlin.reflect.KClass

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

val Number.pc get() = PC(this.toDouble())
