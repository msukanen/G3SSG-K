package net.linkpc.scifi.g3ssg

import net.linkpc.scifi.g3ssg.adapter.convert
import net.linkpc.scifi.g3ssg.core.C
import net.linkpc.scifi.g3ssg.core.K
import org.junit.Assert
import org.junit.Test

class TemperatureTest {
    @Test
    fun temp_conversionWorks() {
        Assert.assertEquals(K.AbsoluteZero.toDouble(), C.AbsoluteZero.convert<K>().toDouble(), 0.0001)
    }
}
