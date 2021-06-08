package semanticAnalyzer;

import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

import java.util.LinkedList;

public class SemanticFunctionToken extends SemanticToken{
    private LinkedList<Tag> functionParams;

    public SemanticFunctionToken(Token token, SemanticType type, LinkedList<Tag> functionParams){
        super(token, type);
        this.functionParams = functionParams;
    }

    public LinkedList<Tag> getFunctionParams(){
        return this.functionParams;
    }

    @Override
    public String toString() {
        return "\nToken name: " + this.getMark() +
                " --- Actual value: " + this.getValue() +
                " --- Scope: " + this.getScope() +
                " - Type: " + this.getType() +
                " - Params: " + this.functionParams;
    }
}
