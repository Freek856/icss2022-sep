package nl.han.ica.icss.parser;

import java.awt.*;
import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private HANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
		currentContainer.push(ast.root);
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}
	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		ast.root = (Stylesheet) currentContainer.pop();
	}
	@Override
	//-----stylerule + SELECTORS-----
	public void enterStylerule(ICSSParser.StyleruleContext ctx){
		Stylerule stylerule = new Stylerule();
		if (ctx.getChild(0).getText().startsWith("#")) {
			stylerule.addChild(new IdSelector(ctx.getChild(0).getText()));
		} else if (ctx.getChild(0).getText().startsWith(".")) {
			stylerule.addChild(new ClassSelector(ctx.getChild(0).getText()));
		} else {
			stylerule.addChild(new TagSelector(ctx.getChild(0).getText()));
		}
		currentContainer.push(stylerule);
	}

	public void exitStylerule(ICSSParser.StyleruleContext ctx){
		ASTNode stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx){
		VariableAssignment variableAssignment = new VariableAssignment();
		currentContainer.push(variableAssignment);
	}

	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx){
		VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
		currentContainer.peek().addChild(variableAssignment);
	}

	//-----Declaration-----
	public void enterDeclaration(ICSSParser.DeclarationContext ctx){
		Declaration declaration = new Declaration(ctx.getText());
		currentContainer.push(declaration);
	}
	public void exitDeclaration(ICSSParser.DeclarationContext ctx){
		ASTNode declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	//-----Property-----
	public void enterProperty(ICSSParser.PropertyContext ctx){
		PropertyName propertyName = new PropertyName(ctx.getText());
		currentContainer.push(propertyName);
	}
	public void exitProperty(ICSSParser.PropertyContext ctx){
		ASTNode propertyName = (PropertyName) currentContainer.pop();
		currentContainer.peek().addChild(propertyName);
	}

	//-----OPERATIONS-----
	@Override
	public void enterAddOrSubstractOperation(ICSSParser.AddOrSubstractOperationContext ctx){
		if (ctx.getChild(1).getText().equals("+")) {
			currentContainer.push(new AddOperation());
		}
		if (ctx.getChild(1).getText().equals("-")) {
			currentContainer.push(new SubtractOperation());
		}
	}
	public void exitAddOrSubstractOperation(ICSSParser.AddOrSubstractOperationContext ctx){
		Operation operation = (Operation) currentContainer.pop();
		currentContainer.peek().addChild(operation);
	}

	public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx){
		MultiplyOperation multiplyOperation = new MultiplyOperation();
		currentContainer.push(multiplyOperation);
	}
	public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx){
		MultiplyOperation multiplyOperation = (MultiplyOperation) currentContainer.pop();
		currentContainer.peek().addChild(multiplyOperation);
	}

	//-----LITERALS-----
	//-----PixelLiteral-----
	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelLiteral);
	}
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx){
		if(currentContainer.peek() instanceof VariableReference){
			VariableReference variableReference = (VariableReference) currentContainer.pop();
			currentContainer.peek().addChild(variableReference);
		}
		else {
			ASTNode pixelLiteral = (PixelLiteral) currentContainer.pop();
			currentContainer.peek().addChild(pixelLiteral);
		}
	}

	//-----ColorLiteral-----
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
		currentContainer.push(colorLiteral);
	}

	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx){
		ASTNode colorLiteral = (ColorLiteral) currentContainer.pop();
		currentContainer.peek().addChild(colorLiteral);
	}

	//-----BoolLiteral-----
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		BoolLiteral boolLiteral = new BoolLiteral(ctx.getText());
		currentContainer.push(boolLiteral);
	}

	public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx){
		ASTNode boolLiteral = (BoolLiteral) currentContainer.pop();
		currentContainer.peek().addChild(boolLiteral);
	}

	//-----ScalarLiteral-----
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
		currentContainer.push(scalarLiteral);
	}

	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx){
		ASTNode scalarLiteral = (ScalarLiteral) currentContainer.pop();
		currentContainer.peek().addChild(scalarLiteral);
	}

	//-----ScalarLiteral-----
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.getText());
		currentContainer.push(percentageLiteral);
	}

	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx){
		ASTNode percentageLiteral = (PercentageLiteral) currentContainer.pop();
		currentContainer.peek().addChild(percentageLiteral);
	}

	//-----OVERIGE-----
	//-----VariableReference-----
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx){
		VariableReference variableReference = new VariableReference(ctx.getText());
		currentContainer.push(variableReference);
	}

	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx){
		ASTNode variableReference = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(variableReference);
	}

	//-----IF EN ELSE-----
	//-----IF-----
	public void enterIfClause(ICSSParser.IfClauseContext ctx){
		IfClause ifClause = new IfClause();
		currentContainer.push(ifClause);
	}
	public void exitIfClause(ICSSParser.IfClauseContext ctx){
		ASTNode ifClause = (IfClause) currentContainer.pop();
		currentContainer.peek().addChild(ifClause);
	}

	//-----ELSE-----
	public void enterElseClause(ICSSParser.ElseClauseContext ctx){
		ElseClause elseClause = new ElseClause();
		currentContainer.push(elseClause);
	}
	public void exitElseClause(ICSSParser.ElseClauseContext ctx){
		ASTNode elseClause = (ElseClause) currentContainer.pop();
		currentContainer.peek().addChild(elseClause);
	}

}