package lexicalAnalyzer;

public class Token {
	private String name = "";
	private long line;
	private String scope;
	private long column;
	private Tag mark;
	private String value = "";

	Token(Tag mark, long line, long column, String actual_value) {
		this.mark = mark;
		this.line = line;
		this.column = column;
		this.value = actual_value;
	}

	public Token(Tag mark, String value) {
		this.mark = mark;
		this.value = value;
	}

	public Token(Tag mark, String value, String scope) {
		this.mark = mark;
		this.value = value;
		this.scope = scope;
	}

	public Token() {

	}

	public Token(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMark(Tag mark) {
		this.mark = mark;
	}

	public Tag getMark() {
		return mark;
	}

	public long getLine() {
		return line;
	}

	public long getColumn() {
		return column;
	}

	public void setValue(String value) {
		this.value = value;
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
		return obj.mark.equals(this.mark) && obj.value.equals(this.value) && obj.scope.equals(this.scope);
	}

	@Override
	public String toString() {
		return "\nToken name: " + this.mark + " --- Actual value: " + this.value + " --- Position (line:column) "
				+ this.line + ":" + this.column;
	}
}