package ru.spbau.mit.debugger

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.interp.AstExpr
import ru.spbau.mit.interp.Transformer
import ru.spbau.mit.parser.DebugBaseVisitor
import ru.spbau.mit.parser.DebugParser
import ru.spbau.mit.parser.LangLexer
import ru.spbau.mit.parser.LangParser

class Command(private val debugger: Debugger) : DebugBaseVisitor<Unit>() {
    override fun visitLoad(ctx: DebugParser.LoadContext?) {
        ctx ?: error("no load command")

        val lexer = LangLexer(CharStreams.fromFileName(ctx.filename().text))
        val parser = LangParser(BufferedTokenStream(lexer))

        if (parser.numberOfSyntaxErrors > 0) {
            System.err.println("can't parse file")
        }

        try {
            val transformer = Transformer()
            val programAst = transformer.visit(parser.file())
            debugger.setProgramForDebug(programAst)
        } catch (e: Exception) {
            System.err.println("something has broken: ${e.message}")
        }
    }

    override fun visitBreakpoint(ctx: DebugParser.BreakpointContext?) {
        ctx ?: error("no breakpoint command")

        val lineNumber = ctx.line().text.toInt()
        debugger.setBreakpoint(lineNumber)
    }

    override fun visitCondition(ctx: DebugParser.ConditionContext?) {
        ctx ?: error("no condition command")
        try {
            val lineNumber = ctx.line().text.toInt()
            val repr = ctx.expr().text

            val lexer = LangLexer(CharStreams.fromString(repr))
            val parser = LangParser(BufferedTokenStream(lexer))

            if (parser.numberOfSyntaxErrors > 0) {
                println("can't parse condition")
                return
            }

            val expr = (Transformer().visitExpr(parser.expr()) as AstExpr).expr

            debugger.setBreakpoint(lineNumber, expr, repr)
        } catch (e: Exception) {
            System.err.println("something has broken: ${e.message}")
            return
        }
    }

    override fun visitList(ctx: DebugParser.ListContext?) {
        ctx ?: error("no list command")

        for ((line, expr) in debugger.getBreakpoints()) {
            System.err.println("$line:${expr.second}")
        }
    }

    override fun visitRemove(ctx: DebugParser.RemoveContext?) {
        ctx ?: error("no remove command")

        val lineNumber = ctx .line().text.toInt()
        debugger.removeBreakpoint(lineNumber)
    }

    override fun visitRun(ctx: DebugParser.RunContext?) {
        ctx ?: error("no run command")

        debugger.runProgram()
    }

    override fun visitEvaluate(ctx: DebugParser.EvaluateContext?) {
        ctx ?: error("no evaluate command")

        val lexer = LangLexer(CharStreams.fromString(ctx.expr().text))
        val parser = LangParser(BufferedTokenStream(lexer))

        if (parser.numberOfSyntaxErrors > 0) {
            System.err.println("can't parse file")
            return
        }

        try {
            val transformer = Transformer()
            val expressionAst = (transformer.visit(parser.expr()) as AstExpr).expr
            println(debugger.evalExpression(expressionAst))
        } catch (e: Exception) {
            System.err.println("something has broken: ${e.message}")
            return
        }
    }

    override fun visitCmdContinue(ctx: DebugParser.CmdContinueContext?) {
        debugger.continueProgram()
    }

    override fun visitCmdStop(ctx: DebugParser.CmdStopContext?) {
        debugger.stopProgram()
    }
}
