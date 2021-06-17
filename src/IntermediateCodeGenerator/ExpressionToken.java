package IntermediateCodeGenerator;

import lexicalAnalyzer.Token;

public class ExpressionToken extends Token{
    private Token leftOperator;
    private Token ariOperator;
    private Token rightOperator;
    private int expressionScope;

    public ExpressionToken(){
        super();
    }

    public ExpressionToken(Token ariOperator, int expressionScope){
        super();
        this.ariOperator = ariOperator;
        this.expressionScope = expressionScope;
    }

    public ExpressionToken(String _name, Token leftOperator, Token ariOperator, Token rightOperator, int expressionScope) {
        super(_name);
        this.leftOperator = leftOperator;
        this.ariOperator = ariOperator;
        this.rightOperator = rightOperator;
        this.expressionScope = expressionScope;
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
        this.rightOperator = rightOperator;
    }

    public Token getRightOperator() {
        return rightOperator;
    }

    public void setExpressionScope(int expressionScope) {
        this.expressionScope = expressionScope;
    }

    public int getExpressionScope() {
        return expressionScope;
    }

    @Override
    public String toString() {
        String _return = this.getName()+" = ";
        if (leftOperator != null) _return += leftOperator.getValue()+leftOperator.getName()+" ";
        if (ariOperator != null) _return += ariOperator.getValue()+" ";
        if (rightOperator != null) _return += rightOperator.getValue()+rightOperator.getName()+" ";
        return _return;
    }
}
