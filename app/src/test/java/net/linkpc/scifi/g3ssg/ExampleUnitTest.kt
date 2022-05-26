package net.linkpc.scifi.g3ssg

import net.linkpc.scifi.g3ssg.adapter.adaptTo
import net.linkpc.scifi.g3ssg.adapter.adapters
import net.linkpc.scifi.g3ssg.core.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun time_conversionWorks() {
        adapters.add(TimeAdapterD())
        adapters.add(TimeAdapterH())
        assertEquals(1.0, 24.h.adaptTo<Time.Days>().raw, 0.0)
        assertEquals(24.0, 1.d.adaptTo<Time.Hours>().raw, 0.0)
    }
}
