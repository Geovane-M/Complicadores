package ErrorWarnings;

public class EmptyCharacter extends Exception{

	private static final long serialVersionUID = 1L;

	public EmptyCharacter(int line, String column) {
		System.err.println("Error: Empty character constant at line "+line+":\n"+ column);
	}

}
