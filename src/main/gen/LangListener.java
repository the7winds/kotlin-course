// Generated from /home/the7winds/Documents/study/jvm/kotlin-course/src/main/antlr/Lang.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LangParser}.
 */
public interface LangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LangParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(LangParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(LangParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(LangParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(LangParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#blockWithBraces}.
	 * @param ctx the parse tree
	 */
	void enterBlockWithBraces(LangParser.BlockWithBracesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#blockWithBraces}.
	 * @param ctx the parse tree
	 */
	void exitBlockWithBraces(LangParser.BlockWithBracesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(LangParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(LangParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(LangParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(LangParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#funName}.
	 * @param ctx the parse tree
	 */
	void enterFunName(LangParser.FunNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#funName}.
	 * @param ctx the parse tree
	 */
	void exitFunName(LangParser.FunNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(LangParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(LangParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#varName}.
	 * @param ctx the parse tree
	 */
	void enterVarName(LangParser.VarNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#varName}.
	 * @param ctx the parse tree
	 */
	void exitVarName(LangParser.VarNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#parameterNames}.
	 * @param ctx the parse tree
	 */
	void enterParameterNames(LangParser.ParameterNamesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#parameterNames}.
	 * @param ctx the parse tree
	 */
	void exitParameterNames(LangParser.ParameterNamesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(LangParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(LangParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(LangParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(LangParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(LangParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(LangParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(LangParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(LangParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#println}.
	 * @param ctx the parse tree
	 */
	void enterPrintln(LangParser.PrintlnContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#println}.
	 * @param ctx the parse tree
	 */
	void exitPrintln(LangParser.PrintlnContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(LangParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(LangParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(LangParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(LangParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#varLoad}.
	 * @param ctx the parse tree
	 */
	void enterVarLoad(LangParser.VarLoadContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#varLoad}.
	 * @param ctx the parse tree
	 */
	void exitVarLoad(LangParser.VarLoadContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(LangParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(LangParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#level0}.
	 * @param ctx the parse tree
	 */
	void enterLevel0(LangParser.Level0Context ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#level0}.
	 * @param ctx the parse tree
	 */
	void exitLevel0(LangParser.Level0Context ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#level1}.
	 * @param ctx the parse tree
	 */
	void enterLevel1(LangParser.Level1Context ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#level1}.
	 * @param ctx the parse tree
	 */
	void exitLevel1(LangParser.Level1Context ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#level2}.
	 * @param ctx the parse tree
	 */
	void enterLevel2(LangParser.Level2Context ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#level2}.
	 * @param ctx the parse tree
	 */
	void exitLevel2(LangParser.Level2Context ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#level3}.
	 * @param ctx the parse tree
	 */
	void enterLevel3(LangParser.Level3Context ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#level3}.
	 * @param ctx the parse tree
	 */
	void exitLevel3(LangParser.Level3Context ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(LangParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(LangParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LangParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(LangParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link LangParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(LangParser.ConstantContext ctx);
}