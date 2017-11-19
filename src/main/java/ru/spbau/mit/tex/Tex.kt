package ru.spbau.mit.tex

import java.io.OutputStream

@DslMarker
annotation class TexMarker

@TexMarker
abstract class TexElement {
    private val source: StringBuilder = StringBuilder()

    override fun toString() = source.toString()

    open operator fun String.unaryPlus() {
        source.append(this).append("\n")
    }

    operator fun TexElement.unaryPlus() {
        this@TexElement.source.append(this)
    }

    fun toOutputStream(outputStream: OutputStream) {
        outputStream.write(source.toString().toByteArray())
    }

    infix fun String.to(value: String) = "$this=$value"
}

open class Tag(val name: String, val suffix: String = "") : TexElement()

fun <T : Tag> T.initTag(init: T.() -> Unit): T {
    +("\\$name" + suffix)
    init()
    return this
}

fun <T : Tag> T.initScope(init: T.() -> Unit): T {
    + ("\\begin{$name}$suffix")
    init()
    + "\\end{$name}"
    return this
}


class DocumentClass(name: String) : Tag("documentclass", "{$name}")

class Usepackage(name: String, vararg args: String) : Tag("usepackage", "${argsString(*args)}{$name}")

class Frame : Tag("frame")

class Item : Tag("item")

class Math : Tag("math")

class Align : Tag("align")


class Enumerate : Tag("enumerate") {
    fun item(init: DocumentScope.() -> Unit) {
        val item = Item()
        + item.initTag {
            val scope = DocumentScope()
            scope.init()
            + scope
        }
    }
}

class Itemize : Tag("itemize") {
    fun item(init: DocumentScope.() -> Unit) {
        val item = Item()
        + item.initTag {
            val scope = DocumentScope()
            scope.init()
            + scope
        }
    }
}

class Document : Tag("document")

class DocumentScope : TexElement() {
    fun itemize(init: Itemize.() -> Unit) {
        + Itemize().initScope(init)
    }

    fun enumerate(init: Enumerate.() -> Unit) {
        + Enumerate().initScope(init)
    }

    fun math(init: Tag.() -> Unit) {
        + Math().initScope(init)
    }

    fun align(init: Tag.() -> Unit) {
        + Align().initScope(init)
    }

    fun customTag(name: String, vararg args: String, init: Tag.() -> Unit) {
        + Tag(name, argsString(*args)).initScope(init)
    }

    fun frame(frameTitle: String, init: DocumentScope.() -> Unit) {
        val frame = Frame()
        frame.initScope {
            + "\\frametitle{$frameTitle}"
            val scope = DocumentScope()
            scope.init()
            + scope
        }
        + frame
    }
}
