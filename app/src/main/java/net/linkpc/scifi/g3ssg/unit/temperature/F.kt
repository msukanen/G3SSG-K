package net.linkpc.scifi.g3ssg.unit.temperature

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.unit.Temperature
import kotlin.reflect.KClass

class F(guts:Double) : CoreTemperature() {
    constructor() : this(C().convert<F>().toDouble())
    override var raw: Double = guts
        set(value) {
            field = if (value < K0) K0 else value
        }
    class FtoXAdapter : Adapter {
        override fun <T : Any> canAdapt(from: Any, to: KClass<T>): Boolean =
            from is F && (to == K::class || to == F::class || to == C::class)

        override fun <T : Any> convert(from: Any, to: KClass<T>): T {
            require(canAdapt(from, to))
            val a = from as F
            return when(to) {
                F::class -> a
                K::class -> convert<C>().convert<K>()
                else -> C((a.raw - 32.0) * (5.0/9.0))
            } as T
        }
    }

    override fun compareTo(other: Temperature): Int = toDouble().compareTo(other.convert<F>().toDouble())
    operator fun plus(other: Number): F = F(raw + other.toDouble())
    operator fun plus(other: Temperature): F = F(raw + other.convert<F>().toDouble())
    operator fun minus(other: Number): F = F(raw - other.toDouble())
    operator fun minus(other: Temperature): F = F(raw - other.convert<F>().toDouble())

    companion object {
        private const val K0 = -459.67
        init {
            adapters.add(FtoXAdapter())
        }
        val Zero = F()
        val AbsoluteZero: F = F(K0)
    }
}

val Number.F get() = F(this.toDouble())
