package net.linkpc.scifi.g3ssg.core.orbital.planet.biosphere

import net.linkpc.scifi.g3ssg.core.Star
import net.linkpc.scifi.g3ssg.core.TL
import net.linkpc.scifi.g3ssg.core.d6
import net.linkpc.scifi.g3ssg.core.orbital.Planet
import net.linkpc.scifi.g3ssg.core.orbital.Terrestrial

interface Biosphere {
    companion object {
        fun create(s: Star, p: Terrestrial, baseTL: Int): Biosphere =
            when (3.d6 + s.lifeRollMod) {
                in -10..7 -> NoLife()
                in 8..9 -> ProtoOrganisms()
                10 -> LowerPlants()
                11 -> HigherPlants()
                in 12..13 -> LowerAnimals()
                in 14..16 -> HigherAnimals()
                17 -> NearIntelligence()
                else -> Intelligence(baseTL, p)
            }
    }
}

class NoLife : Biosphere
class ProtoOrganisms : Biosphere
class LowerPlants : Biosphere
class HigherPlants : Biosphere
class LowerAnimals : Biosphere
class HigherAnimals : Biosphere
class NearIntelligence : Biosphere
class Intelligence(baseTL: Int, p:Planet) : Biosphere {
    val tl: TL = TL.create(baseTL)
}
