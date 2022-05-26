package net.linkpc.scifi.g3ssg.core

import kotlin.random.Random

/**
 * Created by Markku Sukanen
 * Updated: 25.5.2022.
 */
abstract class RNG {
    companion object {
        private val rng = Random(System.currentTimeMillis())

        /**
         * Generate a bounds inclusive (pseudo-)random integer.
         *
         * @param from minimum value
         * @param to maximum value
         * @return Int within (inclusive) {@code from} and {@code to}.
         */
        fun range(from: Int, to: Int): Int = rng.nextInt(to - from) + from

        /**
         * Generate a random integer by emulating a die cast.
         *
         * @param sides number of sides of a die
         * @return Int
         */
        private fun any1D(sides: Int): Int = range(1, sides)

        /**
         * Generate a random integer by emulating dice cast.
         *
         * @param sides number of sides per die
         * @param num number of dice to roll
         * @return Int
         */
        internal fun anyD(sides: Int, num: Int = 1): Int {
            var result = 0
            for (i in 1..num)
                result += any1D(sides)
            return result
        }
    }
}

val Int.d6 get() = RNG.anyD(6,this)
val Int.d20 get() = RNG.anyD(20,this)
//val Int.asMultiplierDouble: Double get() = 0.01 * this
//fun Int.upTo(maxInclusive: Int) = Dice.range(this, maxInclusive)
//fun Int.ifChance(chance: Int) = if(1.d100 <= chance) this else 0
