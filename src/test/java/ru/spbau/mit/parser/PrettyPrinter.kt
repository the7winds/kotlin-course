package ru.spbau.mit.parser

import org.antlr.v4.runtime.ParserRuleContext
import ru.spbau.mit.parser.LangParser.*

class PrettyPrinter : LangBaseVisitor<String>() {

    override fun visitFile(ctx: FileContext?): String = visit(ctx?.block())

    override fun visitBlock(ctx: BlockContext?): String =
            ctx?.statement()?.joinToString(separator = "\n") { visit(it) } ?: "<null>"


    override fun visitBlockWithBraces(ctx: BlockWithBracesContext?): String =
            arrayOf("{", visit(ctx?.block()), "}").joinToString(separator = "\n")


    override fun visitStatement(ctx: StatementContext?): String {
        return visit(ctx?.getRuleContext(ParserRuleContext::class.java, 0) ?: error("no statement"))
    }

    override fun visitFunction(ctx: FunctionContext?): String {
        val name = visit(ctx?.funName())
        val args = visit(ctx?.parameterNames())
        val body = visit(ctx?.blockWithBraces())
        return "fun $name($args) $body"
    }

    override fun visitFunName(ctx: FunNameContext?): String = ctx?.text ?: "<null>"

    override fun visitVariable(ctx: VariableContext?): String {
        val varName = visit(ctx?.varName())

        return if (ctx?.expr() != null) {
            val expr = visit(ctx.expr())
            "var $varName = $expr"
        } else {
            "var $varName"
        }
    }

    override fun visitVarName(ctx: VarNameContext?): String = ctx?.text ?: "<null>"

    override fun visitParameterNames(ctx: ParameterNamesContext?): String =
            ctx?.identifier()?.joinToString(transform = { visit(it) }) ?: "<null>"

    override fun visitWhileStatement(ctx: LangParser.WhileStatementContext?): String {
        val condition = visit(ctx?.expr())
        val body = visit(ctx?.blockWithBraces())
        return "while ($condition) $body"
    }

    override fun visitIfStatement(ctx: IfStatementContext?): String {
        val condition = visit(ctx?.expr())
        val thenBody = visit(ctx?.blockWithBraces(0))
        val elseParsed = ctx?.blockWithBraces(1)
        val elseBody = if (elseParsed != null) { "else ${visit(elseParsed)}" } else ""
        return "if ($condition) $thenBody $elseBody"
    }

    override fun visitAssignment(ctx: AssignmentContext?): String =
            "${ctx?.identifier()?.text} = ${visit(ctx?.expr())}"

    override fun visitReturnStatement(ctx: ReturnStatementContext?): String =
            "return ${visit(ctx?.expr())}"

    override fun visitPrintln(ctx: PrintlnContext?): String =
            "println(${visit(ctx?.arguments())})"

    override fun visitFunctionCall(ctx: FunctionCallContext?): String =
            "${visit(ctx?.funName())}(${visit(ctx?.arguments())})"

    override fun visitArguments(ctx: LangParser.ArgumentsContext?): String =
            ctx?.expr()?.joinToString(transform = { visit(it) }) ?: "<null>"

    override fun visitVarLoad(ctx: LangParser.VarLoadContext?): String =
            ctx?.identifier()?.text ?: "<null>"

    override fun visitAtom(ctx: LangParser.AtomContext?): String {
        return visit(ctx?.getRuleContext(ParserRuleContext::class.java, 0) ?: error("no atom"))
    }

    override fun visitLevel0(ctx: LangParser.Level0Context?): String {
        val operands = ctx?.atom()!!.map { visit(it) }
        val operators = ctx.op0()!!.map { it.text }
        return mergeOperators(operands, operators)
    }

    override fun visitLevel1(ctx: LangParser.Level1Context?): String {
        val operands = ctx?.level0()!!.map { visit(it) }
        val operators = ctx.op1()!!.map { it.text }
        return mergeOperators(operands, operators)
    }

    override fun visitLevel2(ctx: LangParser.Level2Context?): String {
        val operands = ctx?.level1()!!.map { visit(it) }
        val operators = ctx.op2()!!.map { it.text }
        return mergeOperators(operands, operators)
    }

    override fun visitLevel3(ctx: LangParser.Level3Context?): String {
        val operands = ctx?.level2()!!.map { visit(it) }
        val operators = ctx.op3()!!.map { it.text }
        return mergeOperators(operands, operators)
    }

    private fun mergeOperators(operands: List<String>, operators: List<String>): String {
        return if (operators.isNotEmpty()) {
            val initial = operands[0]
            val tail = operands.drop(1)
            operators.zip(tail).fold(initial, { acc, (op, r) -> "($acc $op $r)"})
        } else {
            operands[0]
        }
    }

    override fun visitExpr(ctx: LangParser.ExprContext?): String {
        return visit(ctx?.level3())
    }

    override fun visitConstant(ctx: LangParser.ConstantContext?): String {
        return "(${ctx?.text})"
    }
}