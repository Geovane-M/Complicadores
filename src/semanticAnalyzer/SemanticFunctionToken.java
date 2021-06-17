package semanticAnalyzer;

import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

import java.util.LinkedList;

public class SemanticFunctionToken extends SemanticToken{
    private final LinkedList<Tag> functionParams;
    private SemanticType returnType;

    public SemanticFunctionToken(Token token, SemanticType type, LinkedList<Tag> functionParams, SemanticType returnType){
        super(token, type);
        this.functionParams = functionParams;
        this.returnType = returnType;
    }

    public LinkedList<Tag> getFunctionParams(){
        return this.functionParams;
    }

    public SemanticType getReturnType(){
        return this.returnType;
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
