package ru.spbau.mit.debugger

import ru.spbau.mit.interp.*

class Debugger {

    private var scope: Scope? = null
    private var programForDebug: AstNode? = null
    private val breakpoints = HashMap<Int, Pair<ExprNode, String>>()
    private var execution: Iterator<DebugEither>? = null

    private fun reset() {
        scope = Scope(null)
        execution = null
        programForDebug = null
        breakpoints.clear()
    }

    fun setProgramForDebug(program: AstNode) {
        reset()
        programForDebug = program
    }

    fun setBreakpoint(lineNumber: Int, expr: ExprNode = Literal(1), repr: String = "") {
        breakpoints[lineNumber] = expr to repr
    }

    fun getBreakpoints() = breakpoints

    fun removeBreakpoint(lineNumber: Int) {
        breakpoints.remove(lineNumber)
    }

    fun runProgram() {
        if (execution != null) {
            System.err.println("the program is already running")
            return
        }

        scope = Scope(null)
        execution = programForDebug!!.debug(this, scope!!)
        continueProgram()
    }

    fun stopProgram() {
        scope = Scope(null)
        execution = null
    }

    fun continueProgram() {
        if (execution!!.hasNext()) {
            val obj = execution!!.next()
            when (obj) {
                is DebugScope -> scope = obj.scope
                else -> error("impossible")
            }
        } else {
            reset()
        }
    }

    fun evalExpression(expressionAst: ExprNode): Int = expressionAst.eval(scope ?: Scope(null))
}