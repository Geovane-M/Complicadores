package ErrorWarnings;

public class SyntaxError extends Exception{
	private static final long serialVersionUID = 1L;

	public SyntaxError() {
        super("SyntaxError!");
    }

    public SyntaxError(String message) {
        System.err.println(message);
    }
}
