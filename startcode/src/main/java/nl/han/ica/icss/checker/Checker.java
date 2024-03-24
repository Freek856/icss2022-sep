package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
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
                //checkvariableassignment
            }
            else {
                ast.setError("Geen rule of variabel gevonden!");
            }
        }
        variableTypes.removeFirst();
    }

    private void checkStyleRule(Stylerule stylerule) {
        System.out.println("Stylerule: " + stylerule.body.toString());
        for (Selector selector : stylerule.selectors){
            checkSelector(selector);
        }
        for (ASTNode ast : stylerule.body){
            //checkinline
        }
    }

    private void checkSelector(Selector selector){
        if(selector instanceof IdSelector){
            System.out.println("IdSelector: " + selector);
        }
        else if (selector instanceof ClassSelector){
            System.out.println("idSelector: " + selector);
        }
        else if (selector instanceof TagSelector){
            System.out.println("TagSelector: " + selector);
        }
        else {
            System.out.println("Geen selector gevonden");
        }
    }
}