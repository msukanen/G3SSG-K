package net.linkpc.scifi.g3ssg

import net.linkpc.scifi.g3ssg.core.d
import net.linkpc.scifi.g3ssg.core.h
import org.junit.Test

import org.junit.Assert.*

class TimeTest {
    @Test
    fun time_conversionWorks() {
        assertEquals(1.0, 24.h.toDays().toDouble(), 0.0)
        assertEquals(24.0, 1.d.toHours().toDouble(), 0.0)
    }
}
