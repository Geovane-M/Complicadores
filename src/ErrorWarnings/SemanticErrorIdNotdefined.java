package ErrorWarnings;

public class SemanticErrorIdNotdefined extends SemanticError {

	private static final long serialVersionUID = 1L;

	public SemanticErrorIdNotdefined() {
	
	}
	
	public SemanticErrorIdNotdefined(String message, long line, String column) {
		System.err.println("Semantic Error: Identifier " + message + " at line "
				+ line + "\n" + column+ "\nis not defined.\n");
	}

	public SemanticErrorIdNotdefined(String expected, String found, long line, String column) {
		super(expected, found, line, column);
	}

}
