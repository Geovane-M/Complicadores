package ErrorWarnings;

public class ManyCharacters extends Exception{

	private static final long serialVersionUID = 1L;

	public ManyCharacters(int line, String column) {
		System.err.println("LexicalError: Too many characters in character literal at line " +line+":\n"+ column);
	}

}
