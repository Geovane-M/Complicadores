package ErrorWarnings;

public class SemanticErrorIdAlready extends SemanticError {

	private static final long serialVersionUID = 1L;

	public SemanticErrorIdAlready() {
	}

	public SemanticErrorIdAlready(String message, long line, String column) {
		System.err.println("Semantic Error: Identifier " + message + " at line "
				+ line + "\n" + column+ "\nAlready declared in this scope.\n");
	}

	public SemanticErrorIdAlready(String expected, String found, long line, String column) {
		super(expected, found, line, column);
	}

}
