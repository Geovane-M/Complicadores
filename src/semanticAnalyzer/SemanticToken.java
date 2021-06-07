package semanticAnalyzer;

import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

public class SemanticToken extends Token{
	private SemanticType type;
	
	public SemanticToken(Tag mark, String value) {
		super(mark, value);
	}
	
	public SemanticType getType() {
		return type;
	}
	public void setType(SemanticType type) {
		this.type = type;
	}
}
