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

    open operator fun String.unaryMinus() {
        source.append(this)
    }

    fun toOutputStream(outputStream: OutputStream) {
        outputStream.write(source.toString().toByteArray())
    }

    infix fun String.to(value: String) = "$this=$value"


    protected fun <T : Tag> addTag(tag: T, init: T.() -> Unit) {
        tag.run {
            + "\\$name$suffix"
            tag.init()
        }

        this@TexElement.source.append(tag)
    }

    protected fun <T : Tag> addScope(scope: T, init: T.() -> Unit) {
        scope.run {
            + "\\begin{$name}$suffix"
            init()
            + "\\end{$name}"
        }

        this@TexElement.source.append(scope)
    }
}

open class Tag(val name: String, val suffix: String = "") : TexElement()

class DocumentClass(name: String) : Tag("documentclass", "{$name}")

class Usepackage(name: String, vararg args: String) : Tag("usepackage", "${argsString(*args)}{$name}")

class Frame : Tag("frame")

class Item : Tag("item")

class Math : Tag("math")

class Align : Tag("align")


class Enumerate : Tag("enumerate") {
    fun item(init: DocumentScope.() -> Unit) {
        addTag(Item()) {
            - DocumentScope().apply(init).toString()
        }
    }
}

class Itemize : Tag("itemize") {
    fun item(init: DocumentScope.() -> Unit) {
        addTag(Item()) {
            - DocumentScope().apply(init).toString()
        }
    }
}

class Document : Tag("document")

class DocumentScope : TexElement() {
    fun itemize(init: Itemize.() -> Unit) {
        addScope(Itemize(), init)
    }

    fun enumerate(init: Enumerate.() -> Unit) {
        addScope(Enumerate(), init)
    }

    fun math(init: Tag.() -> Unit) {
        addScope(Math(), init)
    }

    fun align(init: Tag.() -> Unit) {
        addScope(Align(), init)
    }

    fun customTag(name: String, vararg args: String, init: Tag.() -> Unit) {
        addScope(Tag(name, argsString(*args)), init)
    }

    fun frame(frameTitle: String, init: DocumentScope.() -> Unit) {
        addScope(Frame()) {
            + "\\frametitle{$frameTitle}"
            - DocumentScope().apply(init).toString()
        }
    }
}
