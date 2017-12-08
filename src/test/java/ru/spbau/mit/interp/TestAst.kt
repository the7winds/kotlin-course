package ru.spbau.mit.interp

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestAst {

    @Test
    fun testIf() {
        val condition = BinOp({ x, y -> if (x < y) 1 else 0 }, LoadVar("c"), Literal(1))
        val thenBody = AstBlock(Meta(0), listOf(AstAssignment(Meta(0), "x", Literal(1))))
        val elseBody = AstBlock(Meta(0), listOf(AstAssignment(Meta(0), "x", Literal(2))))
        val statement = AstIf(Meta(0), condition, thenBody, elseBody)

        val scope = Scope(null)
        scope.addVar("x")
        scope.addVar("c", 0)

        statement.run(scope)

        val thenResult = scope.getVarValue("x")

        assertEquals(1, thenResult)


        scope.setVar("c", 5)

        statement.run(scope)

        val elseResult = scope.getVarValue("x")

        assertEquals(2, elseResult)
    }

    @Test
    fun testWhile() {
        val condition = BinOp({ x, y -> if (x < y) 1 else 0 }, LoadVar("i"), Literal(3))
        val assignment = AstAssignment(Meta(0), "i", BinOp({ x, y -> x + y }, LoadVar("i"), Literal(1)))
        val body = AstBlock(Meta(0), listOf(assignment))
        val statement = AstWhile(Meta(0), condition, body)

        val scope = Scope(null)
        scope.addVar("i", 0)

        statement.run(scope)

        val testResult = scope.getVarValue("i")

        assertEquals(3, testResult)
    }

    @Test
    fun testVarDeclaration() {
        val statement = AstVarDeclaration(Meta(0), "x", BinOp({ x, y -> x + y }, LoadVar("y"), Literal(1)))
        val scope = Scope(null)
        scope.addVar("y", 41)

        statement.run(scope)
        val testResult = scope.getVarValue("x")

        assertEquals(42, testResult)
    }

    @Test
    fun testAssignment() {
        val statement = AstAssignment(Meta(0), "x", BinOp({ x, y -> x + y }, LoadVar("x"), Literal(1)))
        val scope = Scope(null)
        scope.addVar("x", 41)

        statement.run(scope)
        val testResult = scope.getVarValue("x")

        assertEquals(42, testResult)
    }

    @Test
    fun testFunCall() {
        val expr = Call("foo", listOf())
        val body = AstBlock(Meta(0), listOf(
                AstPrintln(Meta(0), listOf(Literal(42))),
                AstReturn(Meta(0), LoadVar("x"))
        ))

        val func = AstFunction(Meta(0), "foo", listOf(), body)

        val scope = Scope(null)
        scope.addFunction(func)
        scope.addVar("x", 13)

        val testResult = expr.eval(scope)

        assertEquals(13, testResult)
    }

    @Test
    fun testLiteral() {
        val expr = Literal(42)
        val scope = Scope(null)

        val testResult = expr.eval(scope)

        assertEquals(42, testResult)
    }

    @Test
    fun testLoadVarSimple() {
        val expr = LoadVar("x")
        val scope = Scope(null)
        scope.addVar("x", 42)

        val testResult = expr.eval(scope)

        assertEquals(42, testResult)
    }

    @Test
    fun testLoadVarEmptyCurrentScope() {
        val expr = LoadVar("x")
        val parentScope = Scope(null)
        parentScope.addVar("x", 42)
        val scope = Scope(parentScope)

        val testResult = expr.eval(scope)

        assertEquals(42, testResult)
    }

    @Test
    fun testLoadVarShadowing() {
        val expr = LoadVar("x")
        val parentScope = Scope(null)
        parentScope.addVar("x", 42)
        val scope = Scope(parentScope)
        scope.addVar("x")

        val testResult = expr.eval(scope)

        assertEquals(0, testResult)
    }

    @Test
    fun testBinOp() {
        val binOp = BinOp({ x, y -> x + y }, BinOp({x, y -> x - y}, Literal(1), Literal(1)), LoadVar("x"))

        val parentScope = Scope(null)
        parentScope.addVar("x", 1)
        val scope = Scope(parentScope)

        val testResult = binOp.eval(scope)

        assertEquals(1, testResult)
    }
}