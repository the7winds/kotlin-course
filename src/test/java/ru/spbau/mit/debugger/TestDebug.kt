package ru.spbau.mit.debugger

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.jupiter.api.Test
import ru.spbau.mit.interp.*
import ru.spbau.mit.parser.LangLexer
import ru.spbau.mit.parser.LangParser
import kotlin.test.assertEquals

class TestDebugger {

    @Test
    fun simpleTest() {
        val source = """
            |var a = 1
            |var b = 2
            |var c
            |c = a + b
            |println(c)
            """.trimMargin()

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val ast = Transformer().visit(parser.block())
        val debugger = Debugger()

        debugger.setProgramForDebug(ast)
        debugger.setBreakpoint(4)
        debugger.setBreakpoint(5)

        debugger.runProgram()

        assertEquals(2, debugger.evalExpression(LoadVar("b")))

        debugger.continueProgram()

        assertEquals(3, debugger.evalExpression(LoadVar("c")))
    }

    @Test
    fun nestedScopesTest() {
        val source = """
            |fun bar() {
            |   var a = 3
            |   return
            |}
            |
            |fun foo() {
            |   var a = 2
            |   bar()
            |}
            |var a = 1
            |
            |foo()
            |
            |println(a)
            """.trimMargin()

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val ast = Transformer().visit(parser.block())
        val debugger = Debugger()

        debugger.setProgramForDebug(ast)
        debugger.setBreakpoint(3)
        debugger.setBreakpoint(8)
        debugger.setBreakpoint(14)

        debugger.runProgram()

        assertEquals(2, debugger.evalExpression(LoadVar("a")))

        debugger.continueProgram()

        assertEquals(3, debugger.evalExpression(LoadVar("a")))

        debugger.continueProgram()

        assertEquals(1, debugger.evalExpression(LoadVar("a")))
    }

    @Test
    fun ifTest() {
        val source = """
            |fun bar() {
            |   var a = 3
            |   return 42
            |}
            |
            |fun foo() {
            |   var b = 2
            |   return
            |}
            |
            |var a = 3
            |
            |if (a < 2) {
            |   bar()
            |} else {
            |   a = 4
            |   foo()
            |}
            |
            |println(a)
            """.trimMargin()

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val ast = Transformer().visit(parser.block())
        val debugger = Debugger()

        debugger.setProgramForDebug(ast)
        debugger.setBreakpoint(3)
        debugger.setBreakpoint(8)
        debugger.setBreakpoint(17)

        debugger.runProgram()

        assertEquals(4, debugger.evalExpression(LoadVar("a")))

        debugger.continueProgram()

        assertEquals(2, debugger.evalExpression(LoadVar("b")))
    }

    @Test
    fun callTest() {
        val source = """
            |fun foo(a) {
            |   return a + 1
            |}
            |
            |var a = foo(1)
            |println(a)
            """.trimMargin()

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val ast = Transformer().visit(parser.block())
        val debugger = Debugger()

        debugger.setProgramForDebug(ast)
        debugger.setBreakpoint(6)

        debugger.runProgram()

        assertEquals(3, debugger.evalExpression(Call("foo", listOf(LoadVar("a")))))
    }

    @Test
    fun conditionTest() {
        val source = """
            |var i = 0
            |
            |while (i < 10) {
            |   i = i + 1
            |   println(i)
            |}
            |
            |println(a)
            """.trimMargin()

        val lexer = LangLexer(CharStreams.fromString(source))
        val parser = LangParser(BufferedTokenStream(lexer))
        val ast = Transformer().visit(parser.block())
        val debugger = Debugger()

        debugger.setProgramForDebug(ast)

        debugger.setBreakpoint(4, BinOp({ x, y -> if (x == y) 1 else 0 }, LoadVar("i"), Literal(5)))
        debugger.setBreakpoint(8)

        debugger.runProgram()

        assertEquals(5, debugger.evalExpression(LoadVar("i")))

        debugger.continueProgram()

        assertEquals(10, debugger.evalExpression(LoadVar("i")))
    }
}