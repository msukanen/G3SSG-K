package net.linkpc.scifi.g3ssg.unit.distance

import net.linkpc.scifi.g3ssg.unit.Distance

sealed class CoreDistance(guts:Double) : Distance, Comparable<Distance> {
    protected var raw: Double = guts
        set(value) {
            field = if(value < 0.0) 0.0 else value
        }
    override fun toDouble() = raw
}
