package ErrorWarnings;

public class SyntaxError extends Exception{
	private static final long serialVersionUID = 1L;

	public SyntaxError() {
        super("SyntaxError!");
    }
	
	public SyntaxError(String message) {
        super(message);
    }

    public SyntaxError(String expected, String found, long line, String column) {
        System.err.println("SyntaxError: Expected '" + expected.toLowerCase() +"' found '"+ found +"' at line " +line+ "\n"+ column);
    }
}
