package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;
import java.util.HashMap;
import java.util.LinkedList;


public class Checker {
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        System.out.println("CHECKEN...");
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet stylesheet){
        variableTypes.addFirst(new HashMap<>());
        for(ASTNode ast : stylesheet.getChildren()){
            if(ast instanceof Stylerule){
                checkStyleRule((Stylerule) ast);
            }
            else if (ast instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) ast);
            }
            else {
                ast.setError("Geen rule of variabel gevonden!");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkStyleRule(Stylerule stylerule) {
        System.out.println("Stylerule: " + stylerule.body.toString());
        for(ASTNode node : stylerule.body){
            if(node instanceof Declaration){
                checkDeclaration((Declaration) node);
            }
            else if (node instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) node);
            }
            else if (node instanceof IfClause){
                checkIfClause((IfClause) node);
            }
            else{
                node.setError("Geen Declaration, VariableAssignment of IfClause gevonden!");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkDeclaration(Declaration declaration){
        ExpressionType expressionType = checkExpression(declaration.expression);
        if (expressionType != ExpressionType.UNDEFINED){

            switch (declaration.property.name){
                case "background-color":
                    if (expressionType != ExpressionType.COLOR){
                    declaration.setError("Je kan bij backgroundColor alleen een color expressie gebruiken.");
                    }
                    break;
                case "color":
                    if (expressionType != ExpressionType.COLOR){
                    declaration.setError("Je kan bij color alleen een color expressie gebruiken.");
                    }
                    break;
                case "width":
                    if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE){
                    declaration.setError("Je kan bij width alleen een procent of pixel expressie gebruiken.");
                    }
                    break;
                case "height":
                    if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE){
                    declaration.setError("Je kan bij height alleen een procent of pixel expressie gebruiken.");
                    }
                    break;
                default:
                    declaration.setError("Declaration niet gevonden." + declaration.property.name);

            }
//            if(declaration.property.name == "background-color"){
//                if (expressionType != ExpressionType.COLOR){
//                    declaration.setError("Je kan bij backgroundColor alleen een color expressie gebruiken.");
//                }
//            }
//            else if(declaration.property.name == "color"){
//                if (expressionType != ExpressionType.COLOR){
//                    declaration.setError("Je kan bij color alleen een color expressie gebruiken.");
//                }
//            }
//            else if (declaration.property.name == "width"){
//                if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE){
//                    declaration.setError("Je kan bij width alleen een procent of pixel expressie gebruiken.");
//                }
//            }
//            else if (declaration.property.name == "height"){
//                if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE){
//                    declaration.setError("Je kan bij height alleen een procent of pixel expressie gebruiken.");
//                }
//            }
//            else {
//                declaration.setError("Declaration niet gevonden." + declaration.property.name);
//            }
        }
    }

    private ExpressionType checkExpression(Expression expression) {
        if(expression instanceof VariableReference) {
            return checkVariableReference((VariableReference) expression);
        }
        else if (expression instanceof Operation){
            return checkOperationType((Operation) expression);
        }
        else if (expression instanceof Literal){
            return checkLiteral((Literal) expression);
        }
        expression.setError("Geen expressie gevonden" + expression.getClass().getSimpleName());
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkVariableReference(VariableReference variableReference){
        for (HashMap<String, ExpressionType> scope : variableTypes){
            if(scope.containsKey(variableReference.name)){
                return scope.get(variableReference.name);
            }
        }
        variableReference.setError("Variabel is nergens gedefinieerd:" + variableReference.name);
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkOperationType(Operation operation) {
        for (ASTNode child : operation.getChildren()){
            if (child instanceof BoolLiteral){
                child.setError("Bool literals zijn hier niet toegestaan.");
                return ExpressionType.UNDEFINED;
            }
            else if (child instanceof ColorLiteral){
                child.setError("Color literals zijn hier niet toegestaan.");
                return ExpressionType.UNDEFINED;
            }
        }

        if (operation instanceof MultiplyOperation){
            return checkMultiplyOperation((MultiplyOperation) operation);
        }
        else if (operation instanceof AddOperation){
            return checkAddOperation((AddOperation) operation);
        }
        else if (operation instanceof SubtractOperation){
            return checkSubstractOperation((SubtractOperation) operation);
        }
        else {
            operation.setError("Geen correcte operator");
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType checkMultiplyOperation(MultiplyOperation multiplyOperation){
        ExpressionType rightExpression = checkExpression(multiplyOperation.rhs);
        ExpressionType leftExpression = checkExpression(multiplyOperation.lhs);

        if (rightExpression != ExpressionType.SCALAR && leftExpression != ExpressionType.SCALAR){
            multiplyOperation.setError("Je hebt een Scalar expressie odig voor een keer som.");
            return ExpressionType.UNDEFINED;
        }
        if (rightExpression == ExpressionType.SCALAR){
            return leftExpression;
        }else {
            return rightExpression;
        }
    }

    private ExpressionType checkAddOperation(AddOperation addOperation){
        ExpressionType rightExpression = checkExpression(addOperation.rhs);
        ExpressionType leftExpression = checkExpression(addOperation.lhs);

        if (rightExpression == leftExpression){
            return rightExpression;
        }else {
            addOperation.setError("Je kan alleen maar optellen met hetzelfde soort expressie.");
            return ExpressionType.UNDEFINED;
        }

    }

    private ExpressionType checkSubstractOperation(SubtractOperation subtractOperation){
        ExpressionType rightExpression = checkExpression(subtractOperation.rhs);
        ExpressionType leftExpression = checkExpression(subtractOperation.lhs);

        if (rightExpression == leftExpression){
            return rightExpression;
        }else {
            subtractOperation.setError("Je kan alleen maar aftrekken met hetzelfde soort expressie.");
            return ExpressionType.UNDEFINED;
        }
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment){
        variableTypes.getFirst().put(variableAssignment.name.name, checkExpression(variableAssignment.expression));
    }

    private void checkIfClause(IfClause ifClause){
        variableTypes.addFirst(new HashMap<>());

        if (ifClause.conditionalExpression instanceof VariableReference){
            if (checkVariableReference((VariableReference) ifClause.conditionalExpression) != ExpressionType.BOOL){
                ifClause.conditionalExpression.setError("Je kan alleen maar een boolean expressie gebruiken bij een if clause");
            }
        }
        for (ASTNode body : ifClause.body){
            if(body instanceof IfClause){
                checkIfClause((IfClause) body);
            }
            else if (body instanceof ElseClause){
                checkElseClause((ElseClause) body);
            }
            else if (body instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) body);
            }
            else if (body instanceof Declaration){
                checkDeclaration((Declaration) body);
            }
            else {
                body.setError("Je kan alleen een IF, ELSE, Variabel of Declaration in een If clause gebruiken");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkElseClause(ElseClause elseClause) {
        variableTypes.addFirst(new HashMap<>());

        for (ASTNode body : elseClause.body) {
            if (body instanceof IfClause) {
                checkIfClause((IfClause) body);
            } else if (body instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) body);
            } else if (body instanceof Declaration) {
                checkDeclaration((Declaration) body);
            } else {
                body.setError("Je kan alleen een IF, Variabel of Declaration in een If clause gebruiken");
            }
        }
        variableTypes.removeFirst();
    }

    private ExpressionType checkLiteral(Literal literal){
        if (literal instanceof BoolLiteral){
            return ExpressionType.BOOL;
        }
        else if (literal instanceof ColorLiteral){
            return ExpressionType.COLOR;
        }
        else if (literal instanceof PixelLiteral){
            return ExpressionType.PIXEL;
        }
        else if (literal instanceof PercentageLiteral){
            return ExpressionType.PERCENTAGE;
        }
        else if (literal instanceof ScalarLiteral){
            return ExpressionType.SCALAR;
        }
        else {
            literal.setError("Geen literal gevonden: " + literal.getClass().getName());
            return ExpressionType.UNDEFINED;
        }
    }
}