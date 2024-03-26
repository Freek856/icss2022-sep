package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class MultiplyOperation extends Operation {

    @Override
    public String getNodeLabel() {
        return "Multiply";
    }

    @Override
    public Literal calculate() {
        Expression CalculateRhs = rhs.calculate();
        Expression CalculateLhs = lhs.calculate();

        //Bij een keersom zijn er meerdere mogelijkheden, dus hier verandert de IF statement iets
        if ((CalculateRhs instanceof PixelLiteral || CalculateRhs instanceof ScalarLiteral) && (CalculateLhs instanceof PixelLiteral || CalculateLhs instanceof ScalarLiteral)){
            return new PixelLiteral(CalculateRhs.getValue() * CalculateLhs.getValue());
        }
        else if ((CalculateRhs instanceof PercentageLiteral || CalculateRhs instanceof ScalarLiteral) && (CalculateLhs instanceof PercentageLiteral || CalculateLhs instanceof ScalarLiteral)){
            return new PercentageLiteral(CalculateRhs.getValue() * CalculateLhs.getValue());
        }
        return null;
    }
}
