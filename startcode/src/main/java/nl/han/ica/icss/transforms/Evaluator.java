package nl.han.ica.icss.transforms;

import com.google.errorprone.annotations.Var;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import javax.swing.text.Style;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Evaluator implements Transform {

    private final LinkedList<HashMap<String, Literal>> variableValues;
    private final LinkedList<ASTNode> NodesToRemove = new LinkedList<>();

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        Stylesheet stylesheet = ast.root;
        applyStylesheet(stylesheet);

        for (ASTNode node : NodesToRemove){
            stylesheet.removeChild(node);
        }
    }

    private void applyStylesheet(Stylesheet sheet) {
        variableValues.addFirst(new HashMap<>());
        for(ASTNode node : sheet.getChildren()){
            if (node instanceof Stylerule){
                applyStylerule((Stylerule) node);
            }
            else if (node instanceof VariableAssignment){
                evaluateVariableAssignment((VariableAssignment) node);
            }
        }
        variableValues.removeLast();
    }

    private void applyStylerule(Stylerule stylerule) {
        LinkedList<ASTNode> nodes = applyStyleBody(stylerule, stylerule.body);
        for (ASTNode node : nodes){
            stylerule.addChild(node);
        }
        boolean hasIfClause = false;
        for (ASTNode node : stylerule.body) {
            if (node instanceof IfClause) {
                hasIfClause = true;
                break;
            }
        }
        if (hasIfClause){
            applyStylerule(stylerule);
        }
        else {
            applyStyleBody(stylerule, stylerule.body);
        }
    }

    private LinkedList<ASTNode> applyStyleBody(Stylerule stylerule, ArrayList<ASTNode> body) {
        variableValues.addFirst(new HashMap<>());
        LinkedList<ASTNode> nodesToAdd = new LinkedList<>();
        LinkedList<ASTNode> nodesToDelete = new LinkedList<>();
        for (ASTNode node : body) {
            if (node instanceof VariableAssignment){
                nodesToDelete.addAll(evaluateVariableAssignment((VariableAssignment) node));
            }
            else if (node instanceof IfClause){
                evaluateIfClause(stylerule, (IfClause) node, nodesToAdd, nodesToDelete);
            }
            else if (node instanceof Declaration){
                evaluateDeclaration((Declaration) node);
            }
        }
        for (ASTNode node : nodesToDelete){
            stylerule.removeChild(node);
        }
        variableValues.removeFirst();
        return nodesToAdd;
    }

    private LinkedList<ASTNode> evaluateVariableAssignment(VariableAssignment variableAssignment) {
        LinkedList<ASTNode> nodesToRemove = new LinkedList<>();
        if(variableAssignment.expression instanceof PixelLiteral){
            variableValues.getLast().put(variableAssignment.name.name, (PixelLiteral) variableAssignment.expression);
        }
        else  if(variableAssignment.expression instanceof ColorLiteral){
            variableValues.getLast().put(variableAssignment.name.name, (ColorLiteral) variableAssignment.expression);
        }
        else  if(variableAssignment.expression instanceof PercentageLiteral){
            variableValues.getLast().put(variableAssignment.name.name, (PercentageLiteral) variableAssignment.expression);
        }
        else  if(variableAssignment.expression instanceof ScalarLiteral){
            variableValues.getLast().put(variableAssignment.name.name, (ScalarLiteral) variableAssignment.expression);
        }
        else  if(variableAssignment.expression instanceof BoolLiteral){
            variableValues.getLast().put(variableAssignment.name.name, (BoolLiteral) variableAssignment.expression);
        }
        else if (variableAssignment.expression instanceof Operation){
            Literal literal = evaluateOperation((Operation) variableAssignment.expression);
            variableValues.getLast().put(variableAssignment.name.name, literal);
        }
        else if (variableAssignment.expression instanceof VariableReference){
            if (variableValues.getLast().containsKey(((VariableReference) variableAssignment.expression).name)){
                variableValues.getLast().put(variableAssignment.name.name, variableValues.getLast().get(((VariableReference) variableAssignment.expression).name));
            }
            else {
                variableAssignment.setError("Variabel niet gevonden");
            }
        }
        nodesToRemove.add(variableAssignment);
        return nodesToRemove;
    }

    private Literal evaluateOperation(Operation operation) {
        if (operation.rhs instanceof Operation){
            evaluateOperation((Operation) operation.rhs);
        }
        else if (operation.lhs instanceof Operation){
            evaluateOperation((Operation) operation.lhs);
        }
        else if (operation.rhs instanceof VariableReference){
            replaceVariableReference(operation, operation.rhs);
        }
        else if (operation.lhs instanceof VariableReference) {
            replaceVariableReference(operation, operation.lhs);
        }
        return operation.calculate();
    }

    private void replaceVariableReference(Operation operation, Expression expression) {
        if (expression instanceof VariableReference){
            Literal literal = literalOutVariable(((VariableReference) expression).name);
            if (operation.rhs == expression){
                operation.rhs = literal;
            }
            else if (operation.lhs == expression){
                operation.lhs = literal;
            }
        }
    }

    private Literal literalOutVariable(String variable) {
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(variable)) {
                return scope.get(variable);
            }
        }
        return null;
    }

    private void evaluateIfClause(Stylerule stylerule, IfClause ifClause, LinkedList<ASTNode> nodesToAdd, LinkedList<ASTNode> nodesToDelete){
        replaceBoolVariableReference(ifClause, ifClause.conditionalExpression);
        LinkedList<ASTNode> tempNodesToAdd = new LinkedList<>();
        if (ifClause.elseClause != null){
            tempNodesToAdd.addAll(ifClause.elseClause.body);
        }
        else if (((BoolLiteral) ifClause.conditionalExpression).value){
            tempNodesToAdd.addAll(ifClause.body);
        }
        nodesToDelete.add(ifClause);
        if (ifClause.elseClause != null){
            nodesToDelete.add(ifClause.elseClause);
        }
        nodesToAdd.addAll(tempNodesToAdd);
    }

    private void replaceBoolVariableReference(IfClause ifClause, Expression expression) {
        if (expression instanceof VariableReference){
            ifClause.conditionalExpression = literalOutVariable(((VariableReference) expression).name);
        }
    }

    private void evaluateDeclaration(Declaration declaration) {
        if (declaration.expression instanceof Operation) {
            declaration.expression = evaluateOperation((Operation) declaration.expression);
        }
        else if (declaration.expression instanceof VariableReference) {
            Literal literal = literalOutVariable(((VariableReference) declaration.expression).name);
            if (literal != null){
                declaration.expression = literal;
            }
            else {
                declaration.setError("Variabel niet gevonden");
            }
        }
    }

}
