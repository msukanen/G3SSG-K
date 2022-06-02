package net.linkpc.scifi.g3ssg.core.orbital

import net.linkpc.scifi.g3ssg.core.Star
import net.linkpc.scifi.g3ssg.core.d6
import net.linkpc.scifi.g3ssg.unit.distance.AU

interface Orbit {
    val idx: Int
}

interface OrbitalElement : Orbit {
    val distance: AU
}

sealed class Element(override val idx:Int) : Orbit {
    class Empty : Element(0)
}

fun generateOrbits(s: Star): List<Orbit> {
    val es = mutableListOf<Orbit>()

    if (3.d6 <= s.planetsOn) {
        var i = 0
        while(i < s.numPotentialOrbits) {
            i++
            es.add(if (s.distanceOf(i) < s.biozone.first) {
                when(2.d6) {
                    in 2..4 -> Element.Empty()
                    in 5..6 -> Terrestrial(s, i)
                    in 7..9 -> Terrestrial(s, i)
                    in 10..11 -> AsteroidBelt(s, i)
                    else -> if (i == 1) Element.Empty() else GasGiant(s, i, GasGiant.Type.H)
                }
            } else if (s.distanceOf(i) > s.biozone.second) {
                val mod = if (s.distanceOf(i) > s.biozone.second * 10) 1 else 0
                when(1.d6 +mod) {
                    1 -> Terrestrial(s, i)
                    2 -> AsteroidBelt(s, i)
                    3 -> Element.Empty()
                    7 -> Terrestrial(s, i)
                    else -> GasGiant(s, i)
                }
            } else {
                when (2.d6) {
                    in 2..3 -> Element.Empty()
                    in 4..8 -> Terrestrial(s, i)
                    in 9..10 -> AsteroidBelt(s, i)
                    11 -> GasGiant(s, i, GasGiant.Type.L)
                    else -> GasGiant(s, i, GasGiant.Type.H)
                }
            })
        }
    }

    return es.toList()
}
