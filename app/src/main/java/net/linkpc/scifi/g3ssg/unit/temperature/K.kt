package net.linkpc.scifi.g3ssg.unit.temperature

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.unit.Temperature
import kotlin.reflect.KClass

class K(guts:Double) : CoreTemperature() {
    constructor() : this(0.0)
    override var raw: Double = guts
        set(value) {
            field = if (value < 0.0) 0.0 else value
        }
    class KtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is K && (to == K::class || to == F::class || to == C::class)

        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as K
            return when(to) {
                K::class -> a
                C::class -> C(a.toDouble() + C.K0)
                else -> convert<C>().convert<F>()
            } as T
        }
    }

    override fun compareTo(other: Temperature): Int = toDouble().compareTo(other.convert<K>().toDouble())
    operator fun plus(other: Number): K = K(raw + other.toDouble())
    operator fun plus(other: Temperature): K = K(raw + other.convert<K>().toDouble())
    operator fun minus(other: Number): K = K(raw - other.toDouble())
    operator fun minus(other: Temperature): K = K(raw - other.convert<K>().toDouble())

    companion object {
        init {
            adapters.add(KtoXAdapter())
        }
        val Zero = K()
        val AbsoluteZero = Zero
    }
}

val Number.K get() = K(this.toDouble())
