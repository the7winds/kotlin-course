package ru.spbau.mit.tex

import org.junit.Test
import kotlin.test.assertEquals

class TexTest {
    @Test
    fun testPreambule() {
        val source = file {
            documentClass("article")
            usepackage("babel", "russian")
            usepackage("geometry", "left" to "1.0cm", "right" to "2.0cm")
        }.toString()

        assertEquals(
                """
                    |\documentclass{article}
                    |\usepackage[russian]{babel}
                    |\usepackage[left=1.0cm, right=2.0cm]{geometry}
                    |
                    """.trimMargin(), source)
    }

    @Test
    fun testSimpleDocument() {
        val source = file {
            documentClass("beamer")
            usepackage("babel", "russian")
            document {
                frame("hello") {
                    +"hello world"
                }
            }
        }.toString()

        assertEquals(
                """
                    |\documentclass{beamer}
                    |\usepackage[russian]{babel}
                    |\begin{document}
                    |\begin{frame}
                    |\frametitle{hello}
                    |hello world
                    |\end{frame}
                    |\end{document}
                    |
                    """.trimMargin(), source)
    }

    @Test
    fun testComplexDocument() {
        val source = file {
            documentClass("article")
            usepackage("babel", "russian")
            document {
                + "hello world"

                math { + "E = mc^2" }

                align { + "\\int e^x \\ dx = ex^x + C" }

                + "some other formulas"
                itemize {
                    item { + "$ e^{i\\pi} = -1 $" }

                    item {
                        + "$ \\sin^2 x + \\cos^2 x = 1 $"

                        enumerate {
                            item { + "nested enumerate" }
                            item { + "another item"}
                        }
                    }

                    item {
                        + "kek"
                    }
                }

                + "custom itemize"
                customTag("itemize") {
                    + "\\item custom item 1"
                    + "\\item custom item 2"
                }
            }
        }.toString()

        assertEquals(
                """
                    |\documentclass{article}
                    |\usepackage[russian]{babel}
                    |\begin{document}
                    |hello world
                    |\begin{math}
                    |E = mc^2
                    |\end{math}
                    |\begin{align}
                    |\int e^x \ dx = ex^x + C
                    |\end{align}
                    |some other formulas
                    |\begin{itemize}
                    |\item
                    |$ e^{i\pi} = -1 $
                    |\item
                    |$ \sin^2 x + \cos^2 x = 1 $
                    |\begin{enumerate}
                    |\item
                    |nested enumerate
                    |\item
                    |another item
                    |\end{enumerate}
                    |\item
                    |kek
                    |\end{itemize}
                    |custom itemize
                    |\begin{itemize}
                    |\item custom item 1
                    |\item custom item 2
                    |\end{itemize}
                    |\end{document}
                    |
                    """.trimMargin(), source)
    }
}
