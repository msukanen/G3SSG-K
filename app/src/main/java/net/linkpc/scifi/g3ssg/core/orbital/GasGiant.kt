package net.linkpc.scifi.g3ssg.core.orbital

import net.linkpc.scifi.g3ssg.core.*
import net.linkpc.scifi.g3ssg.core.orbital.planet.Atmosphere

class GasGiant(s: Star, i: Int, t: Type) : CorePlanet(s, i) {
    enum class Type { S, M, L, H, HD }
    enum class SpecialFeature {
        RetrogradeMoon,
        FaintRing,
        SpectacularRing,
        AsteroidBelt,
        OortBelt,
        HabitableMoon
    }

    val destroyerOfWorlds: Boolean = t == Type.HD
    val type = t

    constructor(s: Star, i:Int) : this(s, i, genType(s))

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
    override val axialTilt: Int = axialTilt()
    override val tideLocked: Boolean = false
    override val lenOfDay: Time
    private val _specFeats = mutableMapOf<SpecialFeature, Int>()
    val specialFeatures: List<Pair<SpecialFeature, Int>> = _specFeats.toList()
    override val atmosphere: Atmosphere

    init {
        var mod = when(type) {
            Type.L -> 1
            Type.H -> 2
            Type.HD -> 2
            else -> 0
        }
        _moons[Moon.Type.Moonlet] = 3.d6 + mod
        _moons[Moon.Type.Small] = 2.d6 + mod
        _moons[Moon.Type.Medium] = 1.d6 + 1 + mod
        with(1.d6 -3 +mod) { if (this > 0) _moons[Moon.Type.Large] = this }
        with(1.d6 -5 +mod) { if (this > 0) _moons[Moon.Type.Giant] = this }
        with(1.d6 -7 +mod) { if (this > 0) _moons[Moon.Type.SmallGG] = this }
        lenOfDay = lod(i, radius)
        mod = when(type) {
            Type.L -> 2
            Type.H -> 3
            Type.HD -> 3
            else -> 0
        }
        var n = 1
        while(n > 0) {
            n--
            when(3.d6 +mod) {
                in 3..9 -> continue
                10 -> _specFeats[SpecialFeature.RetrogradeMoon] =
                    _specFeats[SpecialFeature.RetrogradeMoon]?.plus(1) ?: 1
                in 11..13 -> _specFeats[SpecialFeature.FaintRing] =
                    _specFeats[SpecialFeature.FaintRing]?.plus(1) ?: 1
                14 -> _specFeats[SpecialFeature.SpectacularRing] =
                    _specFeats[SpecialFeature.SpectacularRing]?.plus((1)) ?: 1
                15 -> _specFeats[SpecialFeature.AsteroidBelt] =
                    _specFeats[SpecialFeature.AsteroidBelt]?.plus(1) ?: 1
                16 -> _specFeats[SpecialFeature.OortBelt] =
                    _specFeats[SpecialFeature.OortBelt]?.plus(1) ?: 1
                17 -> for ((k, v) in _moons) _moons[k] = v * 2
                18 -> n += 2
                else -> _specFeats[SpecialFeature.HabitableMoon] =
                    _specFeats[SpecialFeature.HabitableMoon]?.plus(1) ?: 1
            }
        }
        atmosphere = Atmosphere.create(s, i, this)
    }

    companion object {
        private fun genType(s: Star): Type {
            var mayBeH = true
            val mod = when (s.type) {
                Star.Type.M -> { mayBeH = false; -2 }
                Star.Type.K -> { mayBeH = false; -1 }
                else -> 0
            }

            while (true)
            {
                return when(3.d6 + mod) {
                    4 -> { if (!mayBeH) { mayBeH = true; continue }; Type.H
                    }
                    in 5..8 -> Type.S
                    in 9..12 -> Type.M
                    in 13..18 -> Type.L
                    else -> { if (!mayBeH) { mayBeH = true; continue }; Type.HD
                    }
                }
            }
        }
    }
}
