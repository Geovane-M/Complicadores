package lexicalAnalyzer;
public class Token{

    private final String Mark;
    private long line;
    private long column;
    private final String value;

    Token(String name, long line, long column, String actual_value){
        this.Mark = name;
        this.line = line;
        this.column = column;
        this.value = actual_value;
    }

    public Token(String name, String value) {
    	this.Mark = name;
    	this.value = value;
    }

	public String getMark() {
		return Mark;
	}

	public long getLine() {
		return line;
	}

	public long getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

	public void setLine(long line) {
		this.line = line;
	}

	public void setColumn(long column) {
		this.column = column;
	}

	@Override
    public String toString(){
        return "Token name: " + this.Mark +
                " --- Actual value: " + this.value + " --- Position (line:column) " + this.line + ":" + this.column;
    }
}