package ErrorWarnings;

public class SemanticError extends Exception{

	private static final long serialVersionUID = 1L;

    public SemanticError(String expected, String found, long line, String column) {
        System.err.println("Semantic Error: Expected '" + expected.toLowerCase() +"' found '"+ found.toLowerCase()  +"' at line " +line+ "\n"+ column);
    }

    public SemanticError(String message, long line, String column) {
        System.err.println(message +" at line " +line+ "\n"+ column);
    }
}
