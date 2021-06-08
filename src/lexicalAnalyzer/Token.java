package lexicalAnalyzer;

public class Token {

	private long line;
	private String scope;
	private long column;
	private final Tag Mark;
	private final String value;

	Token(Tag mark, long line, long column, String actual_value) {
		this.Mark = mark;
		this.line = line;
		this.column = column;
		this.value = actual_value;
	}

	public Token(Tag mark, String value) {
		this.Mark = mark;
		this.value = value;
	}

	public Token(Tag mark, String value, String scope) {
		this.Mark = mark;
		this.value = value;
		this.scope = scope;
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

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public boolean equals(Token obj) {
		return obj.Mark.equals(this.Mark) 
				&& obj.value.equals(this.value) 
				&& obj.scope == this.scope;
	}

	@Override
	public String toString() {
		return "\nToken name: " + this.Mark + " --- Actual value: " + this.value + " --- Position (line:column) "
				+ this.line + ":" + this.column;
	}
}