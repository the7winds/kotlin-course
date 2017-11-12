package ru.spbau.mit.interp

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.LangBaseVisitor
import ru.spbau.mit.parser.LangParser.*
import java.util.*


class Transformer : LangBaseVisitor<AstNode>() {

    override fun visitFile(ctx: FileContext?): AstNode =
            visit(ctx?.block() ?: error("no block found"))

    override fun visitBlock(ctx: BlockContext?): AstNode =
            AstBlock(ctx
                    ?.statement()
                    ?.map { visit(it) } ?: error("no statements found"))

    override fun visitBlockWithBraces(ctx: BlockWithBracesContext?): AstNode =
            visit(ctx?.block() ?: error("no block found"))

    override fun visitStatement(ctx: StatementContext?): AstNode {
        val statement =
                ctx?.function()
                        ?: ctx?.println()
                        ?: ctx?.whileStatement()
                        ?: ctx?.ifStatement()
                        ?: ctx?.assignment()
                        ?: ctx?.variable()
                        ?: ctx?.expr()
                        ?: ctx?.returnStatement()
                        ?: error("impossible statement")

        return visit(statement)
    }

    override fun visitFunction(ctx: FunctionContext?): AstNode {
        val name = ctx?.funName()?.Identifier()?.text ?: error("function has no name")

        val signature = ctx.parameterNames()?.Identifier()?.map {
            it.text ?: error("parameter has no name")
        } ?: error("null signature")

        val body = visit(ctx.blockWithBraces()) as AstBlock

        return AstFunction(name, signature, body)
    }

    override fun visitVariable(ctx: VariableContext?): AstNode {
        val varName = ctx?.varName()?.text ?: error("variable has no name")
        val expr = if (ctx.expr() != null) { visit(ctx.expr()) } else { null }
        return AstVarDeclaration(varName, expr?.toExpr())
    }

    override fun visitWhileStatement(ctx: WhileStatementContext?): AstNode {
        val expr = visit(ctx?.expr() ?: error("while has no condition"))
        val body = visit(ctx.blockWithBraces() ?: error("while has no body")) as AstBlock
        return AstWhile(expr.toExpr(), body)
    }

    override fun visitIfStatement(ctx: IfStatementContext?): AstNode {
        val expr = visit(ctx?.expr() ?: error("if has no condition"))

        val thenCtx = ctx.blockWithBraces(0)
        val thenBody = visit(thenCtx ?: error("if has no then block")) as AstBlock

        val elseCtx = ctx.blockWithBraces(1)
        val elseBody = if (elseCtx != null) { visit(elseCtx) } else { null } as AstBlock?

        return AstIf(expr.toExpr(), thenBody, elseBody)
    }

    private fun AstNode.toExpr() : ExprNode = (this as AstExpr).expr

    override fun visitAssignment(ctx: AssignmentContext?): AstNode {
        val name = ctx?.Identifier()?.text ?: error("var assignment has no name")
        val expr = visit(ctx.expr() ?: error("var assignment has no expression"))
        return AstAssignment(name, expr.toExpr())
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext?): AstNode {
        val expr = if (ctx?.expr() != null) { visit(ctx.expr()) } else { null }
        return AstReturn(expr?.toExpr())
    }

    override fun visitFunctionCall(ctx: FunctionCallContext?): AstNode {
        val name = ctx?.funName()?.Identifier()?.text ?: error("function call has no name")
        val args = ctx.arguments()?.expr()?.map {
            visit(it).toExpr()
        } ?: error("function call args is null")

        return AstExpr(Call(name, args))
    }

    override fun visitVarLoad(ctx: VarLoadContext?): AstNode {
        return AstExpr(LoadVar(ctx?.text ?: error("var load has no name")))
    }

    override fun visitAtom(ctx: AtomContext?): AstNode {
        val atom = ctx?.constant()
                ?: ctx?.functionCall()
                ?: ctx?.varLoad()
                ?: ctx?.expr()
                ?: error("impossible atom")

        return visit(atom)
    }

    private fun <T: ParserRuleContext> mergeOperators(opSwitcher: (String) -> ((Int, Int) -> Int), operators: List<TerminalNode>, operands: List<T>): ExprNode {
        val initial = visit(operands[0]).toExpr()
        val tail = operands.subList(1, operands.size)
        return operators.zip(tail).fold(initial, { lExpr, (opToken, r) ->
            val rExpr = visit(r).toExpr()
            val op = opSwitcher(opToken.text)
            BinOp(op, lExpr, rExpr)
        })
    }

    override fun visitLevel0(ctx: Level0Context?): AstNode {
        val expr = mergeOperators({
            when (it) {
                "*" -> { x, y -> x * y }
                "/" -> { x, y -> x / y }
                "%" -> { x, y -> x % y }
                else -> error("parser error")
            }
        },
                ctx?.Op0() ?: error("operators is null"),
                ctx.atom() ?: error("atoms is null"))

        return AstExpr(expr)
    }

    override fun visitLevel1(ctx: Level1Context?): AstNode {
        val expr = mergeOperators({
            when (it) {
                "+" -> { x, y -> x + y }
                "-" -> { x, y -> x - y }
                else -> error("parser error")
            }
        },
                ctx?.Op1() ?: error("operators is null"),
                ctx.level0() ?: error("level0 is null"))

        return AstExpr(expr)
    }

    private val Boolean.int
        get() = if (this) { 1 } else { 0 }

    override fun visitLevel2(ctx: Level2Context?): AstNode {
        val expr = mergeOperators({
            when (it) {
                "<"  -> { x, y -> (x < y).int }
                ">"  -> { x, y -> (x > y).int }
                "<=" -> { x, y -> (x <= y).int }
                ">=" -> { x, y -> (x >= y).int }
                "==" -> { x, y -> (x == y).int }
                "!=" -> { x, y -> (x != y).int }
                else -> error("parser error")
            }
        },
                ctx?.Op2() ?: error("operators is null"),
                ctx.level1() ?: error("level1 is null"))

        return AstExpr(expr)
    }

    override fun visitLevel3(ctx: Level3Context?): AstNode {
        val expr = mergeOperators({
            when (it) {
                "&&"  -> { x, y -> (x != 0 && y != 0).int }
                "||"  -> { x, y -> (x != 0 || y != 0).int }
                else -> error("parser error")
            }
        },
                ctx?.Op3() ?: error("operators is null"),
                ctx.level2() ?: error("level2 is null"))

        return AstExpr(expr)
    }

    override fun visitExpr(ctx: ExprContext?): AstNode = visit(ctx?.level3() ?: error("level3 is null"))

    override fun visitConstant(ctx: ConstantContext?): AstNode =
            AstExpr(Literal(ctx?.value ?: error("constant is null")))

    override fun visitPrintln(ctx: PrintlnContext?): AstNode {
        val args = ctx?.arguments()?.expr()
                ?.map { visit(it).toExpr() } ?: Collections.emptyList()

        return AstPrintln(args)
    }

    override fun visitErrorNode(node: ErrorNode?): AstNode =
            error("error node")
}