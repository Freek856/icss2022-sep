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
	//-----stylerule-----
	public void enterStylerule(ICSSParser.StyleruleContext ctx){
		Stylerule stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	public void exitStylerule(ICSSParser.StyleruleContext ctx){
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	//-----tagSelector-----
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx){
		Selector selector = new TagSelector(ctx.getText());
		currentContainer.push(selector);
	}

	public void exitTagSelector(ICSSParser.TagSelectorContext ctx){
		ASTNode selector = (Selector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
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
}