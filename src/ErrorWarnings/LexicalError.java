package ErrorWarnings;

public class LexicalError extends Exception {
	private static final long serialVersionUID = 1L;

	public LexicalError() {
		super("LexicalError!");
	}

	public LexicalError(int line, String column) {
		System.err.println("LexicalError: Invalid character at line "+line+":\n"+ column);
	}

}
