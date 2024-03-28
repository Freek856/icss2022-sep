package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet(ast);
	}

	//Genereerd de stylesheet
	private String generateStylesheet(AST ast) {
		StringBuilder StyleSheet = new StringBuilder();
		for (ASTNode node : ast.root.getChildren()){
			if (node instanceof Stylerule){
				//Roept per stylerule die gevonden is een create functie daarvoor aan
				StyleSheet.append(generateStylerool((Stylerule) node));
			}
		}
		return StyleSheet.toString();
	}


	//voegt per stylerule een nieuwe rule toe, met een nieuwe line en met selector ook nog een een { en aan het einde een }
	private String generateStylerool(Stylerule stylerule) {
		StringBuilder StyleRule = new StringBuilder();
		StyleRule.append(stylerule.selectors.get(0).toString()).append(" {\n");
		for (int x = 0; x < stylerule.body.size(); x++){
			StyleRule.append(generateDeclaration((Declaration) stylerule.body.get(x)));
		}
		StyleRule.append("}\n");
		return StyleRule.toString();
	}

	//Genereert de Declarations met al de property, de literals erachter moeten nog uit de expressies gehaald worden in de volgende functie
	private String generateDeclaration(Declaration declaration) {
		StringBuilder Declaration = new StringBuilder();
		for (int y = 0; y < declaration.getChildren().size(); y = y + 2){
			Declaration.append("").append(declaration.property.name.toString()).append(": ");
			Declaration.append(generateLiteral((Literal) declaration.expression)).append(";\n");
		}
		return Declaration.toString();
	}

	//Hier wordt de juiste Literal variant geretourneerd
	private String generateLiteral(Literal literal) {
		if (literal instanceof ColorLiteral){
			return ((ColorLiteral) literal).value;
		}
		else if (literal instanceof PixelLiteral){
			return ((PixelLiteral) literal).value + "px";
		}
		else if (literal instanceof PercentageLiteral){
			return ((PercentageLiteral) literal).value + "%";
		}
		else if (literal instanceof ScalarLiteral){
			return ((ScalarLiteral) literal).value + "";
		}
		else return null;
	}
}
