package semanticAnalyzer;

import lexicalAnalyzer.Token;

public class SemanticToken extends Token {
    private final SemanticType type;

    public SemanticToken(Token token, SemanticType type) {
        super(token.getMark(), token.getValue(), token.getScope());
        this.type = type;
    }

    public SemanticType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "\nToken name: " + this.getMark() +
                " --- Actual value: " + this.getValue() +
                " --- Scope: " + this.getScope() +
                " - Type: " + this.type;
    }
}
