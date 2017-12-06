package ru.spbau.mit.interp

import java.io.PrintStream

class VariableNotFoundException(varName: String) :
        Exception("variable \"$varName\" not found")

class FunctionNotFoundException(funName: String) :
        Exception("variable \"$funName\" not found")


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
            parent?.setVar(name, v) ?: throw VariableNotFoundException("name")
        }
    }

    fun addFunction(astFunction: AstFunction) {
        functions[astFunction.name] = astFunction
    }
}

interface AstNode {
    fun run(scope: Scope): Int?
}

class AstFunction(val name: String, val signature: List<String>, val block: AstBlock) : AstNode {
    override fun run(scope: Scope): Int? {
        scope.addFunction(this)
        return null
    }
}

class AstBlock(private val statements: List<AstNode>) : AstNode {
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

class AstExpr(val expr: ExprNode) : AstNode {
    override fun run(scope: Scope): Int? {
        expr.eval(scope)
        return null
    }
}

class AstReturn(private val expr: ExprNode? = null) : AstNode {
    override fun run(scope: Scope): Int?  = expr?.eval(scope) ?: 0
}

class AstVarDeclaration(private val name: String, private val expr: ExprNode? = null) : AstNode {
    override fun run(scope: Scope): Int? {
        scope.addVar(name, expr?.eval(scope) ?: 0)
        return null
    }
}

class AstAssignment(private val name: String, private val expr: ExprNode) : AstNode {
    override fun run(scope: Scope): Int? {
        scope.setVar(name, expr.eval(scope))
        return null
    }
}

class AstIf(private val condition: ExprNode,
            private val thenBlock: AstBlock,
            private val elseBlock: AstBlock? = null) : AstNode {
    override fun run(scope: Scope): Int? =
            if (condition.eval(scope) != 0) { thenBlock } else { elseBlock }?.run(Scope(scope))
}

class AstWhile(private val condition: ExprNode, private val block: AstBlock) : AstNode {
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

class AstPrintln(private val args: List<ExprNode>) : AstNode {
    override fun run(scope: Scope): Int? {
        val outputString = args.joinToString(separator = " ") { it.eval(scope).toString() }
        scope.output.println(outputString)
        return null
    }
}

interface ExprNode {
    fun eval(scope: Scope) : Int
}

class BinOp(private val op: (Int, Int) -> Int,
            private val left: ExprNode,
            private val right: ExprNode) : ExprNode {
    override fun eval(scope: Scope): Int = op(left.eval(scope), right.eval(scope))
}

class Call(private val name: String, private val args: List<ExprNode>) : ExprNode {
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
    override fun eval(scope: Scope): Int = v
}

class LoadVar(private val name: String) : ExprNode {
    override fun eval(scope: Scope): Int = scope.getVarValue(name)
}