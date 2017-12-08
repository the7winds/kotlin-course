package ru.spbau.mit.interp

import ru.spbau.mit.debugger.Debugger
import java.io.PrintStream
import kotlin.coroutines.experimental.buildIterator

class VariableNotFoundException(varName: String) :
        Exception("variable \"$varName\" not found")

class FunctionNotFoundException(funName: String) :
        Exception("function \"$funName\" not found")

class Scope(private val parent: Scope?, output: PrintStream? = null) {
    private var varsValues: MutableMap<String, Int> = HashMap()
    private var functions: MutableMap<String, AstFunction> = HashMap()

    val output: PrintStream = output ?: parent?.output ?: System.out

    fun getVarValue(name: String): Int = varsValues[name]
            ?: parent?.getVarValue(name)
            ?: throw VariableNotFoundException(name)

    fun getFunction(name: String): AstFunction = functions[name]
            ?: parent?.getFunction(name)
            ?: throw FunctionNotFoundException(name)

    fun addVar(name: String, v: Int = 0) {
        varsValues[name] = v
    }

    fun setVar(name: String, v: Int) {
        if (varsValues.containsKey(name)) {
            varsValues[name] = v
        } else {
            parent?.setVar(name, v) ?: throw VariableNotFoundException(name)
        }
    }

    fun addFunction(astFunction: AstFunction) {
        functions[astFunction.name] = astFunction
    }
}

sealed class DebugEither
data class DebugScope(val scope: Scope): DebugEither()
data class DebugValue(val v: Int?): DebugEither()

data class Meta(val lineNumber: Int)

abstract class AstNode(private val meta: Meta) {
    open protected fun test(context: Debugger, scope: Scope): Iterator<DebugEither> = buildIterator {
        if (context.getBreakpoints()[meta.lineNumber]?.first?.eval(scope) ?: 0 != 0) {
            yield(DebugScope(scope))
        }
    }

    abstract protected fun innerDebug(context: Debugger, scope: Scope): Iterator<DebugEither>

    fun debug(context: Debugger, scope: Scope): Iterator<DebugEither> = buildIterator {
        yieldAll(test(context, scope))
        yieldAll(innerDebug(context, scope))
    }

    abstract fun run(scope: Scope): Int?
}

class AstFunction(meta: Meta, val name: String, val signature: List<String>, val block: AstBlock) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope): Iterator<DebugEither> = buildIterator {
        yield(DebugValue(run(scope)))
    }

    override fun run(scope: Scope): Int? {
        scope.addFunction(this)
        return null
    }
}

class AstBlock(meta: Meta, private val statements: List<AstNode>) : AstNode(meta) {
    // we should stop at more specific statements
    override fun test(context: Debugger, scope: Scope): Iterator<DebugEither> = buildIterator {  }

    override fun innerDebug(context: Debugger, scope: Scope) = buildIterator {
        for (statement in statements) {
            val seq = statement.debug(context, scope)
            for (obj in seq) {
                when (obj) {
                    is DebugValue -> if (obj.v != null) {
                        yield(obj)
                        return@buildIterator
                    }
                    is DebugScope -> yield(obj)
                }
            }
        }
    }

    override fun run(scope: Scope): Int? {
        statements.forEach {
            val ret = it.run(scope)
            if (ret != null) {
                return ret
            }
        }

        return null
    }
}

class AstExpr(meta: Meta, val expr: ExprNode) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope) = buildIterator {
        val eLast = Last(expr.debug(context, scope))
        yieldAll(eLast.process())
    }

    override fun run(scope: Scope): Int? {
        expr.eval(scope)
        return null
    }
}

class AstReturn(meta: Meta, private val expr: ExprNode? = null) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope) =
            expr?.debug(context, scope) ?: buildIterator { yield(DebugValue(0)) }

    override fun run(scope: Scope): Int? = expr?.eval(scope) ?: 0
}

class AstVarDeclaration(meta: Meta, private val name: String, private val expr: ExprNode? = null) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope) = buildIterator {
        val value = if (expr != null) {
            val eLast = Last(expr.debug(context, scope))
            yieldAll(eLast.process())
            eLast.v
        } else 0

        scope.addVar(name, value)

        yield(DebugValue(null))
    }

    override fun run(scope: Scope): Int? {
        scope.addVar(name, expr?.eval(scope) ?: 0)
        return null
    }
}

class AstAssignment(meta: Meta, private val name: String, private val expr: ExprNode) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope) = buildIterator {
        val eLast = Last(expr.debug(context, scope))
        yieldAll(eLast.process())
        scope.setVar(name, eLast.v)
    }

    override fun run(scope: Scope): Int? {
        scope.setVar(name, expr.eval(scope))
        return null
    }
}

class AstIf(meta: Meta,
            private val condition: ExprNode,
            private val thenBlock: AstBlock,
            private val elseBlock: AstBlock? = null) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope) = buildIterator {
        val conditionLast = Last(condition.debug(context, scope))

        yieldAll(conditionLast.process())

        if (conditionLast.v != 0) {
            yieldAll(thenBlock.debug(context, scope))
        } else if (elseBlock != null){
            yieldAll(elseBlock.debug(context, scope))
        }
    }

    override fun run(scope: Scope): Int? {
        return if (condition.eval(scope) != 0) {
            thenBlock
        } else {
            elseBlock
        }?.run(Scope(scope))
    }
}

class AstWhile(meta: Meta, private val condition: ExprNode, private val block: AstBlock) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope) = buildIterator {
        while (true) {
            val conditionLast = Last(condition.debug(context, scope))

            yieldAll(conditionLast.process())

            if (conditionLast.v == 0) {
                yield(DebugValue(null))
                return@buildIterator
            }

            block.debug(context, Scope(scope)).forEach {
                when (it) {
                    is DebugScope -> yield(it)
                    is DebugValue -> if (it.v != null) {
                        yield(it)
                        return@buildIterator
                    }
                }
            }
        }
    }

    override fun run(scope: Scope): Int? {
        while (condition.eval(scope) != 0) {
            val ret = block.run(Scope(scope))
            if (ret != null) {
                return ret
            }
        }

        return null
    }
}

class AstPrintln(meta: Meta, private val args: List<ExprNode>) : AstNode(meta) {
    override fun innerDebug(context: Debugger, scope: Scope) = buildIterator {
        val outputString = args.map {
            val eLast = Last(it.debug(context, scope))
            yieldAll(eLast.process())
            eLast.v.toString()
        }.joinToString(separator = " ")

        scope.output.println(outputString)
        yield(DebugValue(null))
    }

    override fun run(scope: Scope): Int? {
        val outputString = args.joinToString(separator = " ") { it.eval(scope).toString() }
        scope.output.println(outputString)
        return null
    }
}

interface ExprNode {
    fun debug(context: Debugger, scope: Scope): Iterator<DebugEither>

    fun eval(scope: Scope) : Int
}

private class Last(private val seq: Iterator<DebugEither>) {
    var v: Int = 0
    fun process() = buildIterator {
        while (seq.hasNext()) {
            val obj = seq.next()
            when (obj) {
                is DebugValue -> if (obj.v != null) v = obj.v
                is DebugScope -> yield(obj)
            }
        }
    }
}

class BinOp(private val op: (Int, Int) -> Int,
            private val left: ExprNode,
            private val right: ExprNode) : ExprNode {

    override fun debug(context: Debugger, scope: Scope) = buildIterator {
        val lLast = Last(left.debug(context, scope))
        yieldAll(lLast.process())

        val rLast = Last(right.debug(context, scope))
        yieldAll(rLast.process())

        yield(DebugValue(op(lLast.v, rLast.v)))
    }

    override fun eval(scope: Scope): Int = op(left.eval(scope), right.eval(scope))
}

class Call(private val name: String, private val args: List<ExprNode>) : ExprNode {
    override fun debug(context: Debugger, scope: Scope) = buildIterator {
        val argsValues = args.map {
            x -> val xLast = Last(x.debug(context, scope))
            yieldAll(xLast.process())
            xLast.v
        }

        val function = scope.getFunction(name)
        val calleeScope = Scope(scope)
        function.signature
                .zip(argsValues)
                .forEach { (name, v) ->
                    calleeScope.addVar(name, v)
                }

        val callLast = Last(function.block.debug(context, calleeScope))
        yieldAll(callLast.process())
        yield(DebugValue(callLast.v))
    }

    override fun eval(scope: Scope): Int {
        val argsValues = args.map { x -> x.eval(scope) }
        val function = scope.getFunction(name)
        val calleeScope = Scope(scope)
        function.signature
                .zip(argsValues)
                .forEach { (name, v) ->
                    calleeScope.addVar(name, v)
                }

        return function.block.run(calleeScope) ?: 0
    }
}

class Literal(private val v: Int) : ExprNode {
    override fun debug(context: Debugger, scope: Scope) = buildIterator { yield(DebugValue(v)) }

    override fun eval(scope: Scope): Int = v
}

class LoadVar(private val name: String) : ExprNode {
    override fun debug(context: Debugger, scope: Scope) = buildIterator { yield(DebugValue(eval(scope))) }

    override fun eval(scope: Scope): Int = scope.getVarValue(name)
}