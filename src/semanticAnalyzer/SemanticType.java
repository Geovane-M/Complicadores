package semanticAnalyzer;

public enum SemanticType {
	INTEGER("integer"), FLOATING_POINT("real"), CHARACTER("character");

	private final String description;

	SemanticType(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return this.description;
	}
}
