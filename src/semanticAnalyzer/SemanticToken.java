package semanticAnalyzer;

import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

public class SemanticToken extends Token {
    private SemanticType type;

    public SemanticToken(Tag mark, String value) {
        super(mark, value);
    }

    public SemanticToken(Token token, SemanticType type) {
        super(token.getMark(), token.getValue(), token.getScope());
        this.type = type;
    }

    public SemanticType getType() {
        return type;
    }

    public void setType(SemanticType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "\nToken name: " + this.getMark() +
                " --- Actual value: " + this.getValue() +
                " --- Scope: " + this.getScope() +
                " - Type: " + this.type;
    }
}
