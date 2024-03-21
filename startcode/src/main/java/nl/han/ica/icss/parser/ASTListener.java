package nl.han.ica.icss.parser;

import java.awt.*;
import java.util.Stack;


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
	//VERANDER DIT NOG TERUG NAAR DE IHANSTACK
	private Stack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new Stack<>();
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
	//-----stylerule-----
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

//	//-----tagSelector-----
//	public void enterTagSelector(ICSSParser.TagSelectorContext ctx){
//		Selector selector = new TagSelector(ctx.getText());
//		currentContainer.push(selector);
//	}
//
//	public void exitTagSelector(ICSSParser.TagSelectorContext ctx){
//		ASTNode selector = (Selector) currentContainer.pop();
//		currentContainer.peek().addChild(selector);
//	}

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

	//-----PixelLiteral-----
	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelLiteral);
	}
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx){
		ASTNode pixelLiteral = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(pixelLiteral);
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

	//-----VariableReference-----
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx){
		VariableReference variableReference = new VariableReference(ctx.getText());
		currentContainer.push(variableReference);
	}

	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx){
		ASTNode variableReference = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(variableReference);
	}
}