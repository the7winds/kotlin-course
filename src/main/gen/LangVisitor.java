// Generated from /home/the7winds/Documents/study/jvm/kotlin-course/src/main/antlr/Lang.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LangParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(LangParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(LangParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#blockWithBraces}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockWithBraces(LangParser.BlockWithBracesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(LangParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(LangParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#funName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunName(LangParser.FunNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(LangParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#varName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarName(LangParser.VarNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#parameterNames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterNames(LangParser.ParameterNamesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(LangParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(LangParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(LangParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(LangParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#println}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintln(LangParser.PrintlnContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(LangParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(LangParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#varLoad}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarLoad(LangParser.VarLoadContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(LangParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#level0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel0(LangParser.Level0Context ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#level1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel1(LangParser.Level1Context ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#level2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel2(LangParser.Level2Context ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#level3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLevel3(LangParser.Level3Context ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(LangParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LangParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(LangParser.ConstantContext ctx);
}