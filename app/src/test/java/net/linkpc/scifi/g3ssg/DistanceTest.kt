package net.linkpc.scifi.g3ssg

import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.core.AU
import net.linkpc.scifi.g3ssg.core.LY
import org.junit.Test

import org.junit.Assert.*

class DistanceTest {
    @Test
    fun distance_conversionWorks() {
        assertEquals(2.0, (1.0.AU * 2).toDouble(), 0.0)
        assertEquals(1.0, 63241.AU.convert<LY>().toDouble(), 0.1)
    }

    @Test
    fun distance_comparisonWorks() {
        assertTrue(1.1.AU < 1.2.AU)
        assertTrue(1.1.AU > 1.09999999999999.AU)
    }
}
