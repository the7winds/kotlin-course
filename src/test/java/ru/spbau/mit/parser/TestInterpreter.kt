package ru.spbau.mit.parser
import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Test
import ru.spbau.mit.interp.Scope
import ru.spbau.mit.interp.Transformer
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals

class TestInterpreter {

    @Test
    fun testBranching() {
        val source = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val transformer = Transformer()

        val testByteArray = ByteArrayOutputStream()
        val testPrintStream = PrintStream(testByteArray, true, "utf-8")

        transformer.visit(parser.block()).run(Scope(null, testPrintStream))

        val testOutput = String(testByteArray.toByteArray(), StandardCharsets.UTF_8)

        val expectedOutput = "0 \n"

        assertEquals(expectedOutput, testOutput)
    }

    @Test
    fun testRecursion() {
        val source = """
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }

            var i = 1
            while (i <= 5) {
                println(i, fib(i))
                i = i + 1
            }
        """

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val transformer = Transformer()

        val testByteArray = ByteArrayOutputStream()
        val testPrintStream = PrintStream(testByteArray, true, "utf-8")

        transformer.visit(parser.block()).run(Scope(null, testPrintStream))

        val testOutput = String(testByteArray.toByteArray(), StandardCharsets.UTF_8)

        val expectedOutput =
                        "1 1 \n" +
                        "2 2 \n" +
                        "3 3 \n" +
                        "4 5 \n" +
                        "5 8 \n"

        assertEquals(expectedOutput, testOutput)
    }

    @Test
    fun testInnerFunction() {
        val source = """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }

                return bar(1)
            }

            println(foo(41)) // prints 42
        """

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val transformer = Transformer()

        val testByteArray = ByteArrayOutputStream()
        val testPrintStream = PrintStream(testByteArray, true, "utf-8")

        transformer.visit(parser.block()).run(Scope(null, testPrintStream))

        val testOutput = String(testByteArray.toByteArray(), StandardCharsets.UTF_8)

        val expectedOutput = "42 \n"

        assertEquals(expectedOutput, testOutput)
    }
}
