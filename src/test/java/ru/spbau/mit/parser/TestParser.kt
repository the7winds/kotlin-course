package ru.spbau.mit.parser

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestParser {

    @Test
    fun testAssociativity() {
        val lexer = LangLexer(CharStreams.fromString("1 - 1 - 1 - 1"))
        val parser = LangParser(BufferedTokenStream(lexer))
        val testString = PrettyPrinter().visit(parser.expr())
        val expectedString = "((((1) - (1)) - (1)) - (1))"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }

    @Test
    fun testSophisticatedExpression() {
        val source = "(z != x) && (x < 0) && (bar() - 1 * foo(x, y) - 1) % (3 / y + 1) < 2 || 1 > x"
        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))

        val testString = PrettyPrinter().visit(parser.expr())
        val expectedString = "((((z != x) && (x < (0))) && ((((bar() - ((1) * foo(x, y))) - (1)) % (((3) / y) + (1))) < (2))) || ((1) > x))"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }

    @Test
    fun testIf() {
        val source = "if (x < 0) { println(42) } else { println(1) }"
        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))

        val testString = PrettyPrinter().visit(parser.ifStatement())
        val expectedString = "if ((x < (0))) {\nprintln((42))\n} else {\nprintln((1))\n}"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }

    @Test
    fun testWhile() {
        val source = "while (1) { println(42) }"
        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))

        val testString = PrettyPrinter().visit(parser.whileStatement())
        val expectedString = "while ((1)) {\nprintln((42))\n}"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }

    @Test
    fun testVarDecl() {
        val source = "var x = 42"
        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))

        val testString = PrettyPrinter().visit(parser.variable())
        val expectedString = "var x = (42)"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }

    @Test
    fun testAssignment() {
        val source = "x = 42 + foo() - bar(y)"
        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))

        val testString = PrettyPrinter().visit(parser.assignment())
        val expectedString = "x = (((42) + foo()) - bar(y))"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }

    @Test
    fun testReturn() {
        val source = "return 42 + foo() - bar(y)"
        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))

        val testString = PrettyPrinter().visit(parser.returnStatement())
        val expectedString = "return (((42) + foo()) - bar(y))"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }

    @Test
    fun testPrintln() {
        val source = "println(42, foo() - bar(y), -1)"
        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))

        val testString = PrettyPrinter().visit(parser.println())
        val expectedString = "println((42), (foo() - bar(y)), (-1))"

        assertEquals(0, parser.numberOfSyntaxErrors)
        assertEquals(expectedString, testString)
    }
}