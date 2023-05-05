package io.github.okafke.aapi.app.util

import org.junit.Assert
import org.junit.Test

class ExtensionTest {
    @Test
    fun testSetIndex() {
        val set = LinkedHashSet<String>()
        set.add("apple")
        set.add("orange")
        set.add("banana")
        Assert.assertEquals("apple", set.index(0))
        Assert.assertEquals("orange", set.index(1))
        Assert.assertEquals("banana", set.index(2))
    }

}