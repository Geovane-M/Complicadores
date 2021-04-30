package ErrorWarnings;

public class SyntaxErrorMain extends Exception{
	private static final long serialVersionUID = 1L;
	
	public SyntaxErrorMain() {
		super("SyntaxError!");
	}

	public SyntaxErrorMain(long line, String column) {
		System.err.println("SyntaxError: Invalid main declaration at line "+line+":\n"+ column);
		//throw new SyntaxErrorMain(this.token.getLine(), this.lexicalAnalyser.lineError() + "\nin token: "+ this.token.getValue());
		//throw new SyntaxErrorMain(this.token.getLine(), this.lexicalAnalyser.lineError());
	}

}
