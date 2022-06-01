package net.linkpc.scifi.g3ssg.core

import net.linkpc.scifi.g3ssg.adapter.Adapter
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.adapter.convert
import kotlin.reflect.KClass

interface Temperature {
    fun toDouble(): Double
}

sealed class CoreTemperature : Temperature, Comparable<Temperature> {
    protected abstract var raw: Double
    override fun toDouble(): Double = raw
}

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
        val AbsoluteZero:F = F(K0)
    }
}

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

val Number.K get() = K(this.toDouble())
val Number.C get() = C(this.toDouble())
val Number.F get() = F(this.toDouble())
