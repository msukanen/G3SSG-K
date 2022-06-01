package net.linkpc.scifi.g3ssg.core.orbital.planet

import net.linkpc.scifi.g3ssg.core.d6
import net.linkpc.scifi.g3ssg.core.orbital.Planet
import net.linkpc.scifi.g3ssg.core.orbital.Terrestrial

class Resources(p:Terrestrial) {
    enum class Type {
        Gemstones, IndustrialCrystals,
        RareMinerals, SpecialMinerals,
        Radioactives, HeavyMetals,
        IndustrialMetals, LightMetals,
        Organics
    }

    enum class Abundance {
        Absent, Scarce, Ample, Plentiful, ExtremelyPlentiful
    }

    val available: Map<Type, Abundance> = abundanceMap(p)

    companion object {
        private fun abundanceMap(p:Terrestrial): Map<Type, Abundance> {
            fun abundanceBy(v:Int) = when(v) {
                in -10..6 -> Abundance.Absent
                in 7..8 -> Abundance.Scarce
                9 -> Abundance.Ample
                in 10..11 -> Abundance.Plentiful
                else -> Abundance.ExtremelyPlentiful
            }
            val mod = when(p.composition) {
                Planet.Composition.Silicate -> -3
                Planet.Composition.LowIron -> -1
                Planet.Composition.HiIron -> 2
                Planet.Composition.Metallic -> 4
                else -> 0
            } + when {
                p.liquidSurface <= 30 -> 1
                p.liquidSurface >= 90 -> -1
                else -> 0
            }
            return mutableMapOf<Type, Abundance>().apply {
                set(Type.Gemstones, abundanceBy(2.d6 + mod - 3))
                set(Type.IndustrialCrystals, abundanceBy(2.d6 +mod -3))
                set(Type.RareMinerals, abundanceBy(2.d6 +mod -2))
                set(Type.SpecialMinerals, abundanceBy(2.d6 +mod -2))
                set(Type.Radioactives, abundanceBy(2.d6 +mod -2))
                set(Type.HeavyMetals, abundanceBy(2.d6 +mod -1))
                set(Type.IndustrialMetals, abundanceBy(2.d6 +mod +1))
                set(Type.LightMetals, abundanceBy(2.d6 +mod +3))
                set(Type.Organics, abundanceBy(2.d6))
            }.toMap()
        }
    }
}
