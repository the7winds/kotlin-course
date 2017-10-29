package ru.spbau.mit
import org.junit.Test
import kotlin.test.assertEquals

class TestSource {
    @Test
    fun testSimpleName() {
        val nameStorage = NameStorage("abc", 2)

        assertEquals("abcabc", nameStorage.name)
    }

    @Test
    fun testSimpleRemove() {
        val nameStorage = NameStorage("abc", 2)

        nameStorage.remove('a', 1)

        assertEquals("bcabc", nameStorage.name)
    }

    @Test
    fun testRemoveRepeatableLettersInAWord() {
        val nameStorage = NameStorage("abca", 2)

        nameStorage.remove('a', 1)
        nameStorage.remove('a', 2)

        assertEquals("bcabca", nameStorage.name)
    }

    @Test
    fun testExampleTest01() {
        val nameStorage = NameStorage("bac", 2)

        nameStorage.remove('a', 2)
        nameStorage.remove('b', 1)
        nameStorage.remove('c', 2)

        assertEquals("acb", nameStorage.name)
    }

    @Test
    fun testExampleTest02() {
        val nameStorage = NameStorage("abacaba", 1)

        nameStorage.remove('a', 1)
        nameStorage.remove('a', 1)
        nameStorage.remove('c', 1)
        nameStorage.remove('b', 2)

        assertEquals("baa", nameStorage.name)
    }
}
