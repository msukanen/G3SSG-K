package net.linkpc.scifi.g3ssg.core

import kotlin.math.sqrt

interface Orbit {
    val idx: Int
}

interface OrbitalElement : Orbit {
    val distance: AU
}

sealed class Moon {
    enum class Type { Moonlet, Small, Medium, Large, Giant, SmallGG }
}

private fun mkAxialTilt() = when(2.d6) {
    in 2..3 -> 0
    in 4..7 -> 1.d6 * 3
    in 8..10 -> 2.d6 + 20
    11 -> 3.d6 + 30
    else -> {
        val t = 1.d6 * 10 + 40
        if (t > 90) 90 else t
    }
}

interface Planet : OrbitalElement {
    enum class Composition { GasGiant, Silicate, LowIron, MedIron, HiIron }

    val radius: Double
    val moons: Map<Moon.Type, Int>
    val density: Double
    val composition: Composition
    val gravity: Double get() = (radius / CorePlanet.EARTH_RAD) * (density / CorePlanet.EARTH_DENSITY)
    val axialTilt: Int
    val lenOfYear: Double
    val lenOfDay: Time
}

sealed class Element(override val idx:Int) : Orbit {
    class Empty : Element(0)
}

class AsteroidBelt(s: Star, i: Int) : Element(i), OrbitalElement {
    enum class Type { C, M, S, IcyC, IcyM, IcyS }

    val type: Type
    init {
        val t: Type
        var icy = false
        while(true) {
            t = when(3.d6) {
                in 3..4 -> Type.M
                in 5..13 -> Type.S
                in 14..17 -> Type.C
                else -> {
                    icy = true
                    continue
                }
            }
            break
        }
        type = when(t) {
            Type.C -> if (icy) Type.IcyC else t
            Type.M -> if (icy) Type.IcyM else t
            else -> if (icy) Type.IcyS else t
        }
    }

    val containsVeryLargeOnes = 5.d6 == 5

    override val distance: AU = s.distanceOf(i)
}

sealed class CorePlanet(s: Star, i: Int) : Element(i), Planet {
    final override val distance: AU = s.distanceOf(i)
    final override val lenOfYear: Double

    init {
        lenOfYear = with(distance.toDouble()) { sqrt((this * this * this) / s.mass) }
    }

    protected val pMoons: MutableMap<Moon.Type, Int> = mutableMapOf()
    override val moons: Map<Moon.Type, Int> = pMoons

    companion object {
        const val EARTH_RAD = 6_371.0
        const val EARTH_DENSITY = 5.5

        @JvmStatic
        protected fun lod(i:Int, radius: Double) = with(when (i) {
                1 -> -4
                2 -> -2
                else -> 0
            } + when {
                radius < EARTH_RAD * 0.5 -> -1
                radius > EARTH_RAD * 9 -> 3
                radius > EARTH_RAD * 6 -> 2
                radius > EARTH_RAD * 3 -> 1
                else -> 0
            }) {
            when (val r = 2.d6 + this) {
                in (-3)..2 -> Time.Days(2.d6 * 10)
                3 -> Time.Days(1.d6 * 12)
                4 -> Time.Days(1.d6 * 5)
                5 -> Time.Hours(2.d6 * 10)
                6 -> Time.Hours(1.d6 * 10)
                in 7..10 -> Time.Hours((14 - r).d6)
                else -> Time.Hours(3.d6)
            }
        }
    }
}

class Terrestrial(s: Star, i: Int) : CorePlanet(s, i) {
    override val radius: Double = 1000.0 * 2.d6
    override val density: Double = 0.1 * 3.d6 + 1.d6
    override val composition: Planet.Composition = when {
        density < 3.1 -> Planet.Composition.Silicate
        density < 4.6 -> Planet.Composition.LowIron
        density < 6.1 -> Planet.Composition.MedIron
        else -> Planet.Composition.HiIron
    }
    override val axialTilt: Int = mkAxialTilt()
    override val lenOfDay: Time
    init {
        val mod = when {
            radius < EARTH_RAD -> -1
            radius > EARTH_RAD*1.5 -> 1
            else -> 0
        }
        with(1.d6 -4 +mod) { if(this>0) pMoons[Moon.Type.Moonlet] = this }
        with(1.d6 -4 +mod) { if(this>0) pMoons[Moon.Type.Small] = this }
        with(1.d6 -5 +mod) { if(this>0) pMoons[Moon.Type.Medium] = this }
        with(1.d6 -5 +mod) { if(this>0) pMoons[Moon.Type.Large] = this }
        lenOfDay = lod(i, radius)
    }
}

class GasGiant(s: Star, i: Int) : CorePlanet(s, i) {
    enum class Type { S, M, L, H }

    var destroyerOfWorlds: Boolean = false
        private set

    val type = when(3.d6 + when(s.type) {
        Star.Type.M -> -2
        Star.Type.K -> -1
        else -> 0
    }) {
        4 -> Type.H
        in 5..8 -> Type.S
        in 9..12 -> Type.M
        in 13..18 -> Type.L
        else -> {
            destroyerOfWorlds = true
            Type.H
        }
    }

    private val _moons: MutableMap<Moon.Type, Int> = mutableMapOf()
    override val moons: Map<Moon.Type, Int> = _moons
    override val radius: Double = when(type) {
        Type.S -> 48_280.32
        Type.M -> 80_467.2
        Type.L -> 128_747.52
        else -> 321_868.8
    }

    override val composition: Planet.Composition = Planet.Composition.GasGiant
    override val density: Double = 0.5 + 0.1 * 1.d20
    override val axialTilt: Int = mkAxialTilt()
    override val lenOfDay: Time

    init {
        val mod = when(type) {
            Type.L -> 1
            Type.H -> 2
            else -> 0
        }
        _moons[Moon.Type.Moonlet] = 3.d6 + mod
        _moons[Moon.Type.Small] = 2.d6 + mod
        _moons[Moon.Type.Medium] = 1.d6 + 1 + mod
        with(1.d6 -3 +mod) { if (this > 0) _moons[Moon.Type.Large] = this }
        with(1.d6 -5 +mod) { if (this > 0) _moons[Moon.Type.Giant] = this }
        with(1.d6 -7 +mod) { if (this > 0) _moons[Moon.Type.SmallGG] = this }
        lenOfDay = lod(i, radius)
    }
}
