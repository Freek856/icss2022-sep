package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class AddOperation extends Operation {

    @Override
    public String getNodeLabel() {
        return "Add";
    }

    @Override
    public Literal calculate() {
        Expression CalculateRhs = rhs.calculate();
        Expression CalculateLhs = lhs.calculate();

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
}
