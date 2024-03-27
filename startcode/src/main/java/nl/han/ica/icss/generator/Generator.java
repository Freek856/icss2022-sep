package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;

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
			StyleRule.append(" ").append(stylerule.body.get(x).toString()).append("\n");
		}
		StyleRule.append("}\n");
		return StyleRule.toString();
	}

}
