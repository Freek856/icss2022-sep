package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;
import java.util.HashMap;
import java.util.LinkedList;


public class Checker {
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        System.out.println("CHECKEN...");
    }

    private void checkStylesheet(Stylesheet stylesheet){
        variableTypes.addFirst(new HashMap<>());
        for(ASTNode ast : stylesheet.getChildren()){
            if(ast instanceof Stylerule){
                //checkstylerule
            }
            else if (ast instanceof VariableAssignment){
                //checkvariableassignment
            }
            else {
                ast.setError("Geen rule of variabel gevonden!");
            }
        }
        variableTypes.removeFirst();
    }
}