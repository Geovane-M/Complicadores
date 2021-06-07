package ErrorWarnings;

public class SemanticError extends Exception{

	private static final long serialVersionUID = 1L;

	public SemanticError() {
        super("Semantic Error!");
    }
	
	public SemanticError(String message) {
        super(message);
    }

    public SemanticError(String expected, String found, long line, String column) {
        System.err.println("Semantic Error: Expected '" + expected.toLowerCase() +"' found '"+ found +"' at line " +line+ "\n"+ column);
    }
}
