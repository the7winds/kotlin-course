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

    var returnValue : Int? = null
        set(value) {
            parent?.returnValue = value
            field = value
        }

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
    fun run(scope: Scope)
}

class AstFunction(val name: String, val signature: List<String>, val block: AstBlock) : AstNode {
    override fun run(scope: Scope) {
        scope.addFunction(this)
    }
}

class AstBlock(private val statements: List<AstNode>) : AstNode {
    override fun run(scope: Scope) {
        statements.forEach {
            it.run(scope)
            if (scope.returnValue != null) {
                return
            }
        }
    }
}

class AstExpr(val expr: ExprNode) : AstNode {
    override fun run(scope: Scope) { expr.eval(scope) }
}

class AstReturn(private val expr: ExprNode? = null) : AstNode {
    override fun run(scope: Scope) {
        scope.returnValue = expr?.eval(scope) ?: 0
    }
}

class AstVarDeclaration(private val name: String, private val expr: ExprNode? = null) : AstNode {
    override fun run(scope: Scope) = scope.addVar(name, expr?.eval(scope) ?: 0)
}

class AstAssignment(private val name: String, private val expr: ExprNode) : AstNode {
    override fun run(scope: Scope) = scope.setVar(name, expr.eval(scope))
}

class AstIf(private val condition: ExprNode,
            private val thenBlock: AstBlock,
            private val elseBlock: AstBlock? = null) : AstNode {
    override fun run(scope: Scope) {
        if (condition.eval(scope) != 0) {
            thenBlock.run(Scope(scope))
        } else {
            elseBlock?.run(Scope(scope))
        }
    }
}

class AstWhile(private val condition: ExprNode, private val block: AstBlock) : AstNode {
    override fun run(scope: Scope) {
        while (condition.eval(scope) != 0) {
            block.run(Scope(scope))
        }
    }
}

class AstPrintln(private val args: List<ExprNode>) : AstNode {
    override fun run(scope: Scope) {
        val outputString = args.joinToString(separator = " ") { it.eval(scope).toString() }
        scope.output.println(outputString)
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

        function.block.run(calleeScope)

        val returnValue = calleeScope.returnValue ?: 0
        calleeScope.returnValue = null

        return returnValue
    }
}

class Literal(private val v: Int) : ExprNode {
    override fun eval(scope: Scope): Int = v
}

class LoadVar(private val name: String) : ExprNode {
    override fun eval(scope: Scope): Int = scope.getVarValue(name)
}