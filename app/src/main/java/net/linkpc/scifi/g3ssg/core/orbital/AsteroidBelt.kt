package net.linkpc.scifi.g3ssg.core.orbital

import net.linkpc.scifi.g3ssg.core.AU
import net.linkpc.scifi.g3ssg.core.Star
import net.linkpc.scifi.g3ssg.core.d6

class AsteroidBelt(s: Star, i: Int) : Element(i), OrbitalElement {
    enum class Type { C, M, S }

    val type: Type
    var icy: Boolean
        private set

    init {
        icy = false
        while(true) {
            type = when(3.d6) {
                in 3..4 -> Type.M
                in 5..13 -> Type.S
                in 14..17 -> Type.C
                else -> {
                    icy = true
                    continue
                }
            }
            break
        }
    }

    val containsVeryLargeOnes = 5.d6 == 5

    override val distance: AU = s.distanceOf(i)
    override fun toString(): String = StringBuilder().apply {
        append("${type}-type ")
        if (icy) append("icy ")
        append("asteroid belt")
    }.toString()
}
