package net.linkpc.scifi.g3ssg.core.orbital

import net.linkpc.scifi.g3ssg.core.Star
import net.linkpc.scifi.g3ssg.core.Time
import net.linkpc.scifi.g3ssg.core.d6
import net.linkpc.scifi.g3ssg.core.orbital.planet.Atmosphere
import net.linkpc.scifi.g3ssg.core.withinBiozoneOf
import java.lang.Double.min

class Terrestrial(s: Star, i: Int) : CorePlanet(s, i) {
    override val radius: Double = 1000.0 * 2.d6
    override val density: Double = 0.1 * 3.d6 + 1.d6
    override val composition: Planet.Composition = when {
        density < 3.1 -> Planet.Composition.Silicate
        density < 4.6 -> Planet.Composition.LowIron
        density < 6.1 -> Planet.Composition.MedIron
        density < 7.1 -> Planet.Composition.HiIron
        else -> Planet.Composition.Metallic
    }
    override val axialTilt: Int = axialTilt()
    override val tideLocked: Boolean =
        when(s.type) {
            Star.Type.M -> true
            Star.Type.K -> s.klass <= Star.Class.V
            else -> false
        }
    override val lenOfDay: Time
    override val atmosphere: Atmosphere
    val liquidSurface: Int
    val humidity: Int
    init {
        val mod = when {
            radius < EARTH_RAD -> -1
            radius > EARTH_RAD *1.5 -> 1
            else -> 0
        }
        with(1.d6 -4 +mod) { if(this>0) pMoons[Moon.Type.Moonlet] = this }
        with(1.d6 -4 +mod) { if(this>0) pMoons[Moon.Type.Small] = this }
        with(1.d6 -5 +mod) { if(this>0) pMoons[Moon.Type.Medium] = this }
        with(1.d6 -5 +mod) { if(this>0) pMoons[Moon.Type.Large] = this }
        lenOfDay = lod(i, radius)
        atmosphere = Atmosphere.create(s, i, this)
        liquidSurface = when {
            atmosphere.category <= Atmosphere.Category.VeryThin -> 0
            s.distanceOf(i).withinBiozoneOf(s) -> (2.d6-2) * 10
            else -> 0
        }
        humidity = when {
            atmosphere.category <= Atmosphere.Category.VeryThin -> 0
            else -> min(2.d6 * 10 + 0.1 * liquidSurface, 100.0).toInt()
        }
    }
    val hostile: Boolean = false
}
