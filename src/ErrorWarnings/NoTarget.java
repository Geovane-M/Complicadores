package ErrorWarnings;

public class NoTarget extends Exception{
	
	private static final long serialVersionUID = 1L;

	public NoTarget() {
		super("\"no target\" in \"no project\"");
	}
}
