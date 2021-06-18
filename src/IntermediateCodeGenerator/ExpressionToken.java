package IntermediateCodeGenerator;

import lexicalAnalyzer.Token;

public class ExpressionToken extends Token{
    private Token leftOperand;
    private Token ariOperator;
    private Token rightOperand;
    private int expressionScope;

    public ExpressionToken(){
        super();
    }

    public ExpressionToken(String _name, Token leftOperand, Token ariOperator, Token rightOperand, int expressionScope) {
        super(_name);
        this.leftOperand = leftOperand;
        this.ariOperator = ariOperator;
        this.rightOperand = rightOperand;
        this.expressionScope = expressionScope;
    }

    public void setLeftOperand(Token leftoperand){
        this.leftOperand = leftoperand;
    }

    public Token getLeftOperand() {
        return leftOperand;
    }

    public void setAriOperand(Token arioperand) {
        this.ariOperator = arioperand;
    }

    public Token getAriOperand() {
        return ariOperator;
    }

    public void setRightOperand(Token rightoperand) {
        this.rightOperand = rightoperand;
    }

    public Token getRightOperand() {
        return rightOperand;
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
        if (leftOperand != null) _return += leftOperand.getValue()+ leftOperand.getName()+" ";
        if (ariOperator != null) _return += ariOperator.getValue()+" ";
        if (rightOperand != null) _return += rightOperand.getValue()+ rightOperand.getName()+" ";
        return _return;
    }
}
