package ru.spbau.mit.interp

class Scope(val parent: Scope?) {
    var returnValue: Int? = null
    var varsValues: MutableMap<String, Int> = HashMap()
    var functions: MutableMap<String, AstFunction> = HashMap()

    fun getVarValue(name: String): Int = varsValues[name] ?: parent?.getVarValue(name)!!

    fun getFunction(name: String): AstFunction = functions[name] ?: parent?.getFunction(name)!!

    fun setOrAddVar(k: String, v: Int) {
        varsValues[k] = v
    }

    fun addFunction(name: String, astFunction: AstFunction) {
        functions[name] = astFunction
    }
}

interface AstNode {
    fun run(scope: Scope)
}

class AstFunction(private val name: String, val signature: List<String>, val body: AstBody) : AstNode {
    override fun run(scope: Scope) {
        scope.addFunction(name, this)
    }
}

class AstBody(private val statements: List<AstNode>) : AstNode {
    override fun run(scope: Scope) {
        statements.forEach {
            it.run(scope)
            if (scope.returnValue != null) {
                return
            }
        }

        scope.returnValue = 0
    }
}

class AstExpr(val expr: ExprNode) : AstNode {
    override fun run(scope: Scope) {}
}

class AstReturn(private val expr: ExprNode? = null) : AstNode {
    override fun run(scope: Scope) {
        scope.returnValue = expr?.eval(scope) ?: 0
    }
}

class AstVarDecl(private val name: String, private val expr: ExprNode? = null) : AstNode {
    override fun run(scope: Scope) = scope.setOrAddVar(name, expr?.eval(scope) ?: 0)
}

class AstAssignment(private val name: String, private val expr: ExprNode) : AstNode {
    override fun run(scope: Scope) = scope.setOrAddVar(name, expr.eval(scope))
}

class AstCondition(private val condition: ExprNode,
                   private val thenBody: AstBody,
                   private val elseBody: AstBody? = null) : AstNode {
    override fun run(scope: Scope) {
        if (condition.eval(scope) > 0) {
            thenBody.run(Scope(scope))
        } else {
            elseBody?.run(Scope(scope))
        }
    }
}

class AstWhile(private val condition: ExprNode, private val body: AstBody) : AstNode {
    override fun run(scope: Scope) {
        while (condition.eval(scope) != 0) {
            body.run(Scope(scope))
        }
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
        val scope = Scope(null)
        function.signature
                .zip(argsValues)
                .forEach { (k, v) ->
                    scope.setOrAddVar(k, v)
                }

        function.body.run(scope)
        return scope.returnValue!!
    }
}

class Literal(private val v: Int) : ExprNode {
    override fun eval(scope: Scope): Int = v
}

class LoadVar(private val name: String) : ExprNode {
    override fun eval(scope: Scope): Int = scope.getVarValue(name)
}