package net.linkpc.scifi.g3ssg.core.orbital.planet

import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.core.*
import net.linkpc.scifi.g3ssg.util.IF

data class Climate (val type:Type) {
    enum class Type {
        Frozen, VeryCold, Cold, Chilly, Cool, EarthNormal, Warm, Tropical, Hot, VeryHot, Infernal
    }

    enum class Range {
        Low, Average, High
    }

    val temperatureRange: Map<Range, Temperature> = tempRangeFor(type)

    companion object {
        fun create(s: Star) = create(s, multiStar = false, moonOfGasGiant = false)
        fun create(s: Star, moonOfGasGiant: Boolean) = create(s, multiStar = false, moonOfGasGiant)
        fun create(s: Star, multiStar: Boolean, moonOfGasGiant: Boolean) =
            when (3.d6 + (-1).IF(multiStar) + 3.IF(moonOfGasGiant) + when(s.type) {
                Star.Type.M -> 2
                Star.Type.K -> 1
                else -> 0
            }) {
                2 -> if (3.d6 < 8) Type.Infernal else Type.VeryHot
                in 3..5 -> Type.VeryHot
                in 6..7 -> Type.Hot
                8 -> Type.Tropical
                9 -> Type.Warm
                10 -> Type.EarthNormal
                11 -> Type.Cool
                12 -> Type.Chilly
                13 -> Type.Cold
                in 14..15 -> Type.VeryCold
                else -> Type.Frozen
            }
        private fun tempRangeFor(t:Type): Map<Range, Temperature> = when(t) {
            Type.Frozen -> mapOf(Range.Low to (-40).F, Range.Average to (-20).F, Range.High to 0.F)
            Type.VeryCold -> tempRangeFor(Type.Frozen) + 20.F
            Type.Cold -> tempRangeFor(Type.VeryCold) + 20.F
            Type.Chilly -> tempRangeFor(Type.Cold) + 20.F
            Type.Cool -> tempRangeFor(Type.Chilly) + 20.F
            Type.EarthNormal -> tempRangeFor(Type.Cool) + 20.F
            Type.Warm -> tempRangeFor(Type.EarthNormal) + 10.F
            Type.Tropical -> tempRangeFor(Type.Warm) + 10.F
            Type.Hot -> tempRangeFor(Type.Tropical) + 10.F
            Type.VeryHot -> tempRangeFor(Type.Hot) + 10.F
            else -> mapOf(Range.Low to 120.F, Range.Average to 160.F, Range.High to 200.F)
        }
    }
}

operator fun Map<Climate.Range, Temperature>.plus(t:Temperature): Map<Climate.Range, Temperature> =
    this.mapValues {
        when (it.value) {
            is K -> t.convert<K>() + it.value
            is C -> t.convert<C>() + it.value
            is F -> t.convert<F>() + it.value
            else -> throw IllegalArgumentException("t:Temperature's underlying type forgotten to implement for Map<R,T>.add(T:Temperature)")
        }
    }
