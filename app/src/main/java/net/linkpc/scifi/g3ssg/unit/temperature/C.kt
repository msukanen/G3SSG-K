package net.linkpc.scifi.g3ssg.unit.temperature

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.unit.Temperature
import kotlin.reflect.KClass

class C(guts:Double) : CoreTemperature() {
    constructor() : this(0.0)
    override var raw: Double = guts
        set(value) {
            field = if (value < -273.15) -273.15 else value
        }
    class CtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is C && (to == K::class || to == F::class || to == C::class)

        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as C
            return when(to) {
                C::class -> a
                K::class -> K(a.raw + 273.15)
                else -> F((a.raw * (9.0/5.0)) + 32.0)
            } as T
        }
    }

    override fun compareTo(other: Temperature): Int = toDouble().compareTo(other.convert<C>().toDouble())
    operator fun plus(other: Number): C = C(raw + other.toDouble())
    operator fun plus(other: Temperature): C = C(raw + other.convert<C>().toDouble())
    operator fun minus(other: Number): C = C(raw - other.toDouble())
    operator fun minus(other: Temperature): C = C(raw - other.convert<C>().toDouble())

    companion object {
        internal const val K0 = -273.15
        init {
            adapters.add(CtoXAdapter())
        }
        val Zero = C()
        val AbsoluteZero = C(K0)
    }
}

val Number.C get() = C(this.toDouble())
