package IntermediateCodeGenerator;

import lexicalAnalyzer.Token;

public class TemporaryToken {
    private String name;
    private Token leftOperator;
    private Token ariOperator;
    private Token RightOperator;
    private int expressionScope;

    public TemporaryToken(){

    }

    public TemporaryToken(Token ariOperator, int expressionScope){
        this.ariOperator = ariOperator;
        this.expressionScope = expressionScope;
    }

    public TemporaryToken(String name, Token leftOperator, Token ariOperator, Token rightOperator, int expressionScope) {
        this.name = name;
        this.leftOperator = leftOperator;
        this.ariOperator = ariOperator;
        RightOperator = rightOperator;
        this.expressionScope = expressionScope;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(String name){
        return this.name;
    }

    public void setLeftOperator(Token leftOperator){
        this.leftOperator = leftOperator;
    }

    public Token getLeftOperator() {
        return leftOperator;
    }

    public void setAriOperator(Token ariOperator) {
        this.ariOperator = ariOperator;
    }

    public Token getAriOperator() {
        return ariOperator;
    }

    public void setRightOperator(Token rightOperator) {
        RightOperator = rightOperator;
    }

    public Token getRightOperator() {
        return RightOperator;
    }

    public void setExpressionScope(int expressionScope) {
        this.expressionScope = expressionScope;
    }

    public int getExpressionScope() {
        return expressionScope;
    }

    @Override
    public String toString() {
        String _return = "";
        if (leftOperator != null) _return += leftOperator.getValue();
        if (ariOperator != null) _return += ariOperator.getValue();
        if (RightOperator != null) _return += RightOperator.getValue();
        return _return;
    }
}
