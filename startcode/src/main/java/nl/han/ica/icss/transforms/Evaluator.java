package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet sheet) {
        applyStylerule((Stylerule) sheet.getChildren().get(0));
    }

    private void applyStylerule(Stylerule stylerule) {
        for (ASTNode node : stylerule.getChildren()){
            if (node instanceof Declaration){
                applyDeclaration((Declaration) node) ;
            }
        }
    }
    private void applyDeclaration(Declaration declaration) {
        declaration.expression = evaluateExpression(declaration.expression);
    }

    private Expression evaluateExpression(Expression expression) {
        if (expression instanceof Literal){
            return (Literal) expression;
        }
        else if (expression instanceof AddOperation){
            return evaluateAddOperation((Operation) expression);
        }
        else if (expression instanceof SubtractOperation){
            return evaluateSubstractOperation((Operation) expression);
        }
        else if (expression instanceof SubtractOperation){
            return evaluateMultiplyOperation((Operation) expression);
        }
        else if (expression instanceof VariableReference){
            return evaluateVariableReference((Operation) expression);
        }
        return expression; //moet deze hier wel?!?!
    }

    private Expression evaluateAddOperation(Operation expression) {
        PixelLiteral left = (PixelLiteral) evaluateExpression(expression.lhs);
        PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
        return new PixelLiteral(left.value + right.value);
    }

    private Expression evaluateSubstractOperation(Operation expression) {
        PixelLiteral left = (PixelLiteral) evaluateExpression(expression.lhs);
        PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
        return new PixelLiteral(left.value - right.value);
    }

    private Expression evaluateMultiplyOperation(Operation expression) {
        PixelLiteral left = (PixelLiteral) evaluateExpression(expression.lhs);
        PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
        return new PixelLiteral(left.value * right.value);
    }

    private Expression evaluateVariableReference(Operation expression){

        return new PixelLiteral();
    }
}
