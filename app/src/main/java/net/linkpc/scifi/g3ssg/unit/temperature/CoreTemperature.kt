package net.linkpc.scifi.g3ssg.unit.temperature

import net.linkpc.scifi.g3ssg.unit.Temperature

sealed class CoreTemperature : Temperature, Comparable<Temperature> {
    protected abstract var raw: Double
    override fun toDouble(): Double = raw
}
