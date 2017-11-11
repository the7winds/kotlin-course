package ru.spbau.mit.interp

import org.antlr.v4.runtime.tree.ErrorNode
import ru.spbau.mit.parser.LangBaseVisitor
import ru.spbau.mit.parser.LangParser.*
import java.util.*


class Transformer : LangBaseVisitor<AstNode>() {

    override fun visitFile(ctx: FileContext?): AstNode = visit(ctx?.block())

    override fun visitBlock(ctx: BlockContext?): AstNode {
        return AstBlock(ctx
                ?.statement()
                ?.map { visit(it) } ?: Collections.emptyList())
    }

    override fun visitBlockWithBraces(ctx: BlockWithBracesContext?): AstNode = visit(ctx?.block())

    override fun visitStatement(ctx: StatementContext?): AstNode {
        val statement =
                ctx?.function()
                        ?: ctx?.println()
                        ?: ctx?.whileStatement()
                        ?: ctx?.ifStatement()
                        ?: ctx?.assignment()
                        ?: ctx?.variable()
                        ?: ctx?.expr()
                        ?: ctx?.returnStatement()!!

        return visit(statement)
    }

    override fun visitFunction(ctx: FunctionContext?): AstNode {
        val name = ctx?.funName()?.Identifier()?.text!!
        val signature = ctx.parameterNames()?.Identifier()?.map { it.text!! }!!
        val body = visit(ctx.blockWithBraces()) as AstBlock

        return AstFunction(name, signature, body)
    }

    override fun visitVariable(ctx: VariableContext?): AstNode {
        val varName = ctx?.varName()?.text!!
        val expr = if (ctx.expr() != null) { visit(ctx.expr()) } else { null } as AstExpr?
        return AstVarDecl(varName, expr?.expr)
    }

    override fun visitWhileStatement(ctx: WhileStatementContext?): AstNode {
        val expr = visit(ctx?.expr()) as AstExpr
        val body = visit(ctx?.blockWithBraces()) as AstBlock
        return AstWhile(expr.expr, body)
    }

    override fun visitIfStatement(ctx: IfStatementContext?): AstNode {
        val expr = visit(ctx?.expr()) as AstExpr
        val thenCtx = ctx?.blockWithBraces(0)
        val elseCtx = ctx?.blockWithBraces(1)
        val thenBody = visit(thenCtx) as AstBlock
        val elseBody = if (elseCtx != null) { visit(elseCtx) } else { null } as AstBlock?
        return AstCondition(expr.expr, thenBody, elseBody)
    }

    override fun visitAssignment(ctx: AssignmentContext?): AstNode {
        val name = ctx?.Identifier()?.text!!
        val expr = visit(ctx.expr()) as AstExpr
        return AstAssignment(name, expr.expr)
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext?): AstNode {
        val expr = if (ctx?.expr() != null) { visit(ctx.expr()) } else { null } as AstExpr?
        return AstReturn(expr?.expr)
    }

    override fun visitFunctionCall(ctx: FunctionCallContext?): AstNode {
        val name = ctx?.funName()?.Identifier()?.text!!
        val args = ctx.arguments()?.expr()?.map { (visit(it) as AstExpr).expr }!!
        return AstExpr(Call(name, args))
    }

    override fun visitVarLoad(ctx: VarLoadContext?): AstNode {
        return AstExpr(LoadVar(ctx!!.text))
    }

    override fun visitAtom(ctx: AtomContext?): AstNode {
        val atom =
                ctx?.constant()
                        ?: ctx?.functionCall()
                        ?: ctx?.varLoad()
        return visit(atom)
    }

    override fun visitLevel0(ctx: Level0Context?): AstNode {
        val operands = ctx!!.atom()
        val initial = (visit(operands[0]) as AstExpr).expr
        val tail = operands.subList(1, operands.size)


        val expr = ctx.Op0()
                .zip(tail)
                .fold(initial, { lExpr, (opToken, r) ->
                    val op: (Int, Int) -> Int = when (opToken.text) {
                        "*" -> { x, y -> x * y }
                        "/" -> { x, y -> x / y }
                        "%" -> { x, y -> x % y }
                        else -> throw Exception("parser error")
                    }
                    val rExpr = (visit(r) as AstExpr).expr
                    BinOp(op, lExpr, rExpr)
                })

        return AstExpr(expr)
    }

    override fun visitLevel1(ctx: Level1Context?): AstNode {
        val operands = ctx!!.level0()
        val initial = (visit(operands[0]) as AstExpr).expr
        val tail = operands.subList(1, operands.size)

        val expr = ctx.Op1()
                .zip(tail)
                .fold(initial, { lExpr, (opToken, r) ->
                    val op: (Int, Int) -> Int = when (opToken.text) {
                        "+" -> { x, y -> x + y }
                        "-" -> { x, y -> x - y }
                        else -> throw Exception("parser error")
                    }
                    val rExpr = (visit(r) as AstExpr).expr
                    BinOp(op, lExpr, rExpr)
                })

        return AstExpr(expr)
    }

    private val Boolean.int
        get() = if (this) { 1 } else { 0 }

    override fun visitLevel2(ctx: Level2Context?): AstNode {
        val operands = ctx!!.level1()
        val initial = (visit(operands[0]) as AstExpr).expr
        val tail = operands.subList(1, operands.size)

        val expr = ctx.Op2()
                .zip(tail)
                .fold(initial, { lExpr, (opToken, r) ->
                    val op: (Int, Int) -> Int = when (opToken.text) {
                        "<"  -> { x, y -> (x < y).int }
                        ">"  -> { x, y -> (x > y).int }
                        "<=" -> { x, y -> (x <= y).int }
                        ">=" -> { x, y -> (x >= y).int }
                        "==" -> { x, y -> (x == y).int }
                        "!=" -> { x, y -> (x != y).int }
                        else -> throw Exception("parser error")
                    }
                    val rExpr = (visit(r) as AstExpr).expr
                    BinOp(op, lExpr, rExpr)
                })

        return AstExpr(expr)
    }

    override fun visitLevel3(ctx: Level3Context?): AstNode {
        val operands = ctx!!.level2()
        val initial = (visit(operands[0]) as AstExpr).expr
        val tail = operands.subList(1, operands.size)


        val expr = ctx.Op3()
                .zip(tail)
                .fold(initial, { lExpr, (opToken, r) ->
                    val op: (Int, Int) -> Int = when (opToken.text) {
                        "&&"  -> { x, y -> if (x != 0 && y != 0) { 1 } else { 0 } }
                        "||"  -> { x, y -> if (x != 0 || y != 0) { 1 } else { 0 } }
                        else -> throw Exception("parser error")
                    }
                    val rExpr = (visit(r) as AstExpr).expr
                    BinOp(op, lExpr, rExpr)
                })

        return AstExpr(expr)
    }

    override fun visitExpr(ctx: ExprContext?): AstNode = visit(ctx!!.level3())

    override fun visitConstant(ctx: ConstantContext?): AstNode = AstExpr(Literal(ctx!!.value))

    override fun visitPrintln(ctx: PrintlnContext?): AstNode {
        val args = ctx?.arguments()
                ?.expr()
                ?.map { (visit(it) as AstExpr).expr } ?: Collections.emptyList()

        return AstPrintln(args)
    }

    override fun visitErrorNode(node: ErrorNode?): AstNode = throw Exception("error")
}