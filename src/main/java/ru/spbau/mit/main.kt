package ru.spbau.mit

import java.util.*
import kotlin.collections.HashSet


/**
 * NameStorage is a wrapper around a name
 * @name - repeatable part of the name
 * @times - times to repeat
 */
class NameStorage(name: String, times: Int) {
    private val kString = (1..times).joinToString(separator = "") { name }
    private val removed = HashSet<Int>()
    private val orders =  mutableMapOf<Char, MutableList<Int>>().apply {
        kString.forEachIndexed { index, c ->
            getOrPut(c, { ArrayList() }).add(index)
        }
    }

    /**
     * @property name returns current name value
     */
    val name: String
        get() = kString.filterIndexed { index, _ -> !removed.contains(index) }

    /**
     * removes [order] occurrence of character [c]
     */
    fun remove(c: Char, order: Int) {
        val order = order - 1
        val toRemove = orders[c]?.get(order) ?: error("incorrect command")
        orders[c]?.removeAt(order)
        removed.add(toRemove)
    }
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val times = scanner.nextInt()
    val name = scanner.next()!!
    val nameStorage = NameStorage(name, times)

    val n = scanner.nextInt()

    for (i in 1..n) {
        val order = scanner.nextInt()
        val c = scanner.next()?.get(0)!!
        nameStorage.remove(c, order)
    }

    println(nameStorage.name)
}
