package semanticAnalyzer;

public enum SemanticType {
	INT("INTEGER"), FLOAT("FLOATING_POINT"), CHAR("CHARACTER");

	private final String description;

	SemanticType(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return this.description;
	}
}
