package lexicalAnalyzer;
public class Token{

    private final Tag Mark;
    private long line;
    private long column;
    private final String value;

    Token(Tag mark, long line, long column, String actual_value){
        this.Mark = mark;
        this.line = line;
        this.column = column;
        this.value = actual_value;
    }

    public Token(Tag mark, String value) {
    	this.Mark = mark;
    	this.value = value;
    }

	public Tag getMark() {
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