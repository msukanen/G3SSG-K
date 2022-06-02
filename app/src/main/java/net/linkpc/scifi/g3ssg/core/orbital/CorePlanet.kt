package net.linkpc.scifi.g3ssg.core.orbital

import net.linkpc.scifi.g3ssg.core.*
import net.linkpc.scifi.g3ssg.core.orbital.planet.Atmosphere
import net.linkpc.scifi.g3ssg.unit.distance.AU
import kotlin.math.sqrt

interface Planet : OrbitalElement {
    enum class Composition { GasGiant, Silicate, LowIron, MedIron, HiIron, Metallic }

    val radius: Double
    val moons: Map<Moon.Type, Int>
    val density: Double
    val composition: Composition
    val gravity: Double get() = (radius / CorePlanet.EARTH_RAD) * (density / CorePlanet.EARTH_DENSITY)
    val axialTilt: Int
    val tideLocked: Boolean
    val lenOfYear: Double
    val lenOfDay: Time
    val atmosphere: Atmosphere
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

        @JvmStatic
        protected fun axialTilt() = when(2.d6) {
            in 2..3 -> 0
            in 4..7 -> 1.d6 * 3
            in 8..10 -> 2.d6 + 20
            11 -> 3.d6 + 30
            else -> {
                val t = 1.d6 * 10 + 40
                if (t > 90) 90 else t
            }
        }
    }
}
