package net.linkpc.scifi.g3ssg.core

import java.lang.Integer.max

sealed class TL(val level: Int) {
    enum class Type {
        All, Science, Art
    }

    enum class Science {
        BiologyAndMedicine, Weaponry, SublightSpaceTravel, PowerGeneration,
        Comms, Sensors, Computers, Robotics, AirTransportation, GroundTransportation,
        FasterThanLightTravel
    }

    enum class Art {
        GamesAndDiversions, SocialScience, History, Mathematics, VisualArts,
        FinanceAndCommerce, PerformingArts, Music, Other
    }

    class Anomalous(lvl: Int) : TL(lvl)
    class Primitive(lvl: Int) : TL(lvl)
    class Modern(lvl: Int) : TL(lvl)
    sealed class TLShifted(lvl: Int, val onlySlightTLShift: Boolean) : TL(lvl)
    open class Retarded(lvl: Int, onlySlightTLShift: Boolean = true) : TLShifted(lvl, onlySlightTLShift)
    class RetardedArt(lvl: Int, val art: Art, onlySlightTLShift: Boolean) : Retarded(lvl, onlySlightTLShift)
    class RetardedScience(lvl: Int, val art: Science, onlySlightTLShift: Boolean) : Retarded(lvl, onlySlightTLShift)
    open class Advanced(lvl: Int, onlySlightTLShift: Boolean = true) : TLShifted(lvl, onlySlightTLShift)
    class AdvancedArt(lvl: Int, val art: Art, onlySlightTLShift: Boolean) : Advanced(lvl, onlySlightTLShift)
    class AdvancedScience(lvl: Int, val art: Science, onlySlightTLShift: Boolean) : Retarded(lvl, onlySlightTLShift)
    class Developing(lvl: Int) : TL(lvl)

    companion object {
        fun create(baseTL: Int): TL = when(3.d6) {
            3 -> Anomalous(1.d6+1)
            in 4..5 -> RetardedScience(baseTL, randomScience(), false)
            in 6..7 -> RetardedArt(baseTL, randomArt(), false)
            in 8..9 -> Primitive(1.d6)
            10 -> Developing(max(baseTL - 1.d6, 1))
            11 -> Retarded(baseTL, true)
            12 -> Modern(baseTL)
            13 -> Advanced(baseTL, true)
            in 14..16 -> AdvancedArt(baseTL, randomArt(), false)
            else -> AdvancedScience(baseTL, randomScience(), false)
        }

        private fun randomScience() = when(2.d6) {
            in 2..4 -> Science.BiologyAndMedicine
            5 -> Science.Weaponry
            6 -> Science.SublightSpaceTravel
            7 -> Science.PowerGeneration
            8 -> if (1.d6 < 4) Science.Comms else Science.Sensors
            9 -> if (1.d6 < 4) Science.Computers else Science.Robotics
            in 10..11 -> if (1.d6 < 4) Science.AirTransportation else Science.GroundTransportation
            else -> Science.FasterThanLightTravel
        }

        private fun randomArt() = when(2.d6) {
            in 2..3 -> Art.GamesAndDiversions
            4 -> if (1.d6 < 4) Art.SocialScience else Art.History
            5 -> Art.Mathematics
            6 -> Art.VisualArts
            7 -> Art.FinanceAndCommerce
            8 -> Art.PerformingArts
            in 9..10 -> Art.Music
            else -> Art.Other
        }
    }
}
