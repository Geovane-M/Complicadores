package ErrorWarnings;

public class SyntaxError extends Exception{
    public SyntaxError() {
        super("SyntaxError!");
    }

    public SyntaxError(String message) {
        System.err.println(message);
    }
}
