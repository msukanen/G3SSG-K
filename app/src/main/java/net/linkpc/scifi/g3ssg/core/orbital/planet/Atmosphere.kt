package net.linkpc.scifi.g3ssg.core.orbital.planet

import net.linkpc.scifi.g3ssg.core.RNG
import net.linkpc.scifi.g3ssg.core.Star
import net.linkpc.scifi.g3ssg.core.d6
import net.linkpc.scifi.g3ssg.core.orbital.CorePlanet
import net.linkpc.scifi.g3ssg.core.orbital.Planet
import net.linkpc.scifi.g3ssg.core.orbital.Terrestrial
import java.lang.Integer.max

data class Atmosphere(val category: Category, val gases: List<Pair<Gas, Int>>) {
    enum class Category {
        None, Trace, VeryThin, Thin, Standard, Dense, VeryDense, Superdense
    }

    enum class Gas(corrosive:Boolean) {
        // Terrestrials
        Reducing(false), OxygenNitrogen(false), Polluted(false),
        // Exotic
        Hydrogen(false), Methane(false), CarbonOxides(false), Nitrogen(false),
        // Potentially corrosives
        Ammonia(true), Chlorine(true),
        Fluorine(true), HiOxygen(true),
        Nitrides(true), SulfurComps(true), WaterVapor(true)
    }

    val pressure: Double = when(category) {
        Category.None -> 0.0
        Category.Trace -> RNG.range(0.01, 0.2)
        Category.VeryThin -> RNG.range(0.15, 0.5)
        Category.Thin -> RNG.range(0.51, 0.8)
        Category.Standard -> RNG.range(0.81, 1.2)
        Category.Dense -> RNG.range(1.21, 1.5)
        Category.VeryDense -> RNG.range(1.51, 5.0)
        else -> RNG.range(5.01, 1000.0) // superdense could be way beyond 1,000x Earth pressure...
    }

    companion object {
        fun create(s: Star, i:Int, p: Planet): Atmosphere {
            val gs = mutableListOf<Gas>()
            // determine gases
            if (p !is Terrestrial) {
                gs.addAll(createEx(s, i))
            } else {
                when(2.d6) {
                    in 2..5 -> gs.add(Gas.Reducing)
                    6 -> gs.addAll(createEx(s, i))
                    in 7..9 -> gs.add(Gas.OxygenNitrogen)
                    10 -> gs.add(Gas.Polluted)
                    else -> gs.addAll(createEx(s, i, 2, true))
                }
            }
            val pr = pressureFor(s, i, p, if (p !is Terrestrial) Category.Superdense else null)
            if (gs.count() == 1)
                return Atmosphere(pr, gs.zip(listOf(100)))
            // determine concentration% per gas
            val cs = mutableListOf<Int>()
            var x = 0
            var cp = 1.d6*10
            var left = 100
            for (g in gs) {
                x++
                when (x) {
                    1 -> {
                        if (g == Gas.HiOxygen && cp < 30)
                            cp = 30
                        left -= cp
                        cs.add(cp)
                    }
                    2-> cs.add(left)
                    else -> cs.add(0)
                }
            }
            return Atmosphere(pr, gs.zip(cs))
        }

        private fun createEx(s: Star, i:Int, minNumGas:Int = 1, forceCorrosive:Boolean = false): List<Gas> {
            fun corrComp(mod:Int) = when(1.d6 +mod) {
                in 1..2 -> Gas.Ammonia
                3 -> Gas.Chlorine
                4 -> Gas.Fluorine
                5 -> Gas.HiOxygen
                6 -> Gas.Nitrides
                7 -> Gas.SulfurComps
                else -> Gas.WaterVapor
            }
            val mod = when {
                s.distanceOf(i) < s.biozone.first -> 2
                s.distanceOf(i) <= s.biozone.second -> 1
                else -> 0
            }
            var n = with(1.d6 - 2) {
                val minGs = max(this, if (forceCorrosive) max(minNumGas, 2) else max(1, minNumGas))
                if (this < minGs) minGs else this
            }
            val gs = mutableListOf<Gas>()
            var fc = forceCorrosive
            while(n > 0) {
                n--
                if (fc) {
                    fc = false
                    gs.add(corrComp(mod))
                }
                gs.add(when(1.d6 +mod) {
                    1 -> Gas.Hydrogen
                    2 -> Gas.Methane
                    3 -> Gas.CarbonOxides
                    4 -> corrComp(mod)
                    else -> Gas.Nitrogen
                })
            }
            return gs.toList()
        }

        private fun pressureFor(s: Star, o:Int, p: Planet, pressure: Category?): Category = when(pressure) {
            null -> {
                val mod = when {
                    s.distanceOf(o) > s.biozone.second * 10 -> -6
                    s.distanceOf(o) > s.biozone.second -> -3
                    else -> 0
                } + when(s.type) {
                    Star.Type.M -> -2
                    Star.Type.K -> -1
                    else -> 0
                } + ((p.radius / (CorePlanet.EARTH_RAD / 20.0)).toInt() - 5)
                when (2.d6 + mod) {
                    in -100..3 -> Category.None
                    4 -> Category.Trace
                    5 -> Category.VeryThin
                    6 -> Category.Thin
                    in 7..9 -> Category.Standard
                    10 -> Category.Dense
                    11 -> Category.VeryDense
                    else -> Category.Superdense
                }
            }
            else -> pressure
        }
    }
}
