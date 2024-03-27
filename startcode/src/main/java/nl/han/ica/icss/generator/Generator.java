package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet(ast);
	}

	private String generateStylesheet(AST ast) {
		StringBuilder StyleSheet = new StringBuilder();
		for (ASTNode node : ast.root.getChildren()){
			if (node instanceof Stylerule){
				StyleSheet.append(generateStylerool((Stylerule) node));
			}
		}
		return StyleSheet.toString();
	}

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
