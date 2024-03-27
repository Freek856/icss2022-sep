package nl.han.ica.icss.transforms;

import com.google.errorprone.annotations.Var;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.*;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        Stylesheet stylesheet = ast.root;
        applyStylesheet(stylesheet);
    }

    private void applyStylesheet(Stylesheet sheet) {
        variableValues.add(new HashMap<>());
        List<ASTNode> nodesToDelete = new ArrayList<>();
        for(ASTNode node : sheet.getChildren()){
            if (node instanceof Stylerule){
                applyStylerule(((Stylerule) node).body);
            }
            else if (node instanceof VariableAssignment){
                evaluateVariableAssignment((VariableAssignment) node);
                nodesToDelete.add(node);
            }
        }
        for (ASTNode node : nodesToDelete){
            sheet.removeChild(node);
        }
        variableValues.removeLast();
    }

    private void applyStylerule(List<ASTNode> stylerule) {
        variableValues.addFirst(new HashMap<>());

        List<ASTNode> nodesToDelete = new ArrayList<>();
        List<ASTNode> nodesToAdd = new ArrayList<>();

        for (ASTNode node : stylerule){
            if (node instanceof Declaration){
                evaluateDeclaration((Declaration) node);
            }
            else if (node instanceof IfClause){
                nodesToAdd.addAll(evaluateIfClause((IfClause) node));
                nodesToDelete.add(node);
            }
            else if (node instanceof VariableAssignment){
                evaluateVariableAssignment((VariableAssignment) node);
                nodesToDelete.add(node);
            }
        }
        for (ASTNode node : nodesToDelete){
            stylerule.remove(node);
        }
        stylerule.addAll(nodesToAdd);
        variableValues.removeFirst();
    }

    private void evaluateDeclaration(Declaration declaration){
        Literal literal = evaluateExpression(declaration.expression);
        assert variableValues.peek() != null;
        variableValues.peek().put(declaration.property.name, literal);
        declaration.expression = literal;
    }
    private List<ASTNode> evaluateIfClause(IfClause ifClause){
        boolean ifIfClause = ((BoolLiteral) Objects.requireNonNull(evaluateExpression(ifClause.conditionalExpression))).value;

        if (!ifIfClause){
            if (ifClause.elseClause == null) {
                return new ArrayList<>();
            }
            applyStylerule(ifClause.elseClause.body);
            return ifClause.elseClause.body;
        }
        applyStylerule(ifClause.body);
        return ifClause.body;
    }

    //Hier check je wat de expressie is en laat je deze berekenen in de operation, of je zet een variabel om naar een waarde
    private Literal evaluateExpression(Expression expression){
        if (expression instanceof Literal){
            return (Literal) expression;
        }
        else if (expression instanceof MultiplyOperation){
            return calculateMultiply((MultiplyOperation) expression);
        }
        else if (expression instanceof AddOperation){
            return calculateAdd((AddOperation) expression);
        }
        else if (expression instanceof SubtractOperation){
            return calculateSubstract((SubtractOperation) expression);
        }
        else if (expression instanceof VariableReference){
            return evaluateVariableReference((VariableReference) expression);
        }
        return null;
    }

    private Literal calculateMultiply(MultiplyOperation operation) {
        Literal CalculateLhs = evaluateExpression(operation.rhs);
        Literal CalculateRhs = evaluateExpression(operation.rhs);

        //Bij een keersom zijn er meerdere mogelijkheden, dus hier verandert de IF statement iets
        if ((CalculateRhs instanceof PixelLiteral || CalculateRhs instanceof ScalarLiteral) && (CalculateLhs instanceof PixelLiteral || CalculateLhs instanceof ScalarLiteral)){
            return new PixelLiteral(CalculateRhs.getValue() * CalculateLhs.getValue());
        }
        else if ((CalculateRhs instanceof PercentageLiteral || CalculateRhs instanceof ScalarLiteral) && (CalculateLhs instanceof PercentageLiteral || CalculateLhs instanceof ScalarLiteral)){
            return new PercentageLiteral(CalculateRhs.getValue() * CalculateLhs.getValue());
        }
        return null;
    }

    private Literal calculateAdd(AddOperation operation) {
        Literal CalculateLhs = evaluateExpression(operation.rhs);
        Literal CalculateRhs = evaluateExpression(operation.rhs);

        if (CalculateRhs instanceof ScalarLiteral && CalculateLhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) CalculateRhs).value + ((ScalarLiteral) CalculateLhs).value);
        }
        else if (CalculateRhs instanceof PixelLiteral && CalculateLhs instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) CalculateRhs).value + ((PixelLiteral) CalculateLhs).value);
        }
        else if (CalculateRhs instanceof PercentageLiteral && CalculateLhs instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) CalculateRhs).value + ((PercentageLiteral) CalculateLhs).value);
        }
        return null;
    }

    private Literal calculateSubstract(SubtractOperation operation) {
        Literal CalculateLhs = evaluateExpression(operation.rhs);
        Literal CalculateRhs = evaluateExpression(operation.rhs);

        if (CalculateRhs instanceof ScalarLiteral && CalculateLhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) CalculateRhs).value - ((ScalarLiteral) CalculateLhs).value);
        }
        else if (CalculateRhs instanceof PixelLiteral && CalculateLhs instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) CalculateRhs).value - ((PixelLiteral) CalculateLhs).value);
        }
        else if (CalculateRhs instanceof PercentageLiteral && CalculateLhs instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) CalculateRhs).value - ((PercentageLiteral) CalculateLhs).value);
        }
        return null;
    }

    //Hier worden de values van de variabelen opgehaald en terug gegeven...
    private Literal evaluateVariableReference(VariableReference variableReference){
        for (HashMap<String, Literal> variableValue : variableValues){
            if (variableValue.containsKey(variableReference.name)){
                return variableValue.get(variableReference.name);
            }
        }
        return null;
    }

    private void evaluateVariableAssignment(VariableAssignment variableAssignment){
        Literal literal = evaluateExpression(variableAssignment.expression);
        assert variableValues.peek() != null;
        variableValues.peek().put(variableAssignment.name.name, literal);
        variableAssignment.expression = literal;
    }

}
