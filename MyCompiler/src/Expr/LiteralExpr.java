package Expr;

import tokenizer.Token;

public class LiteralExpr implements Expr {

    private LiteralType literalType;
    private Token value;

    public LiteralType getLiteralType() {
        return literalType;
    }

    public Token getValue() {
        return value;
    }

    public LiteralExpr(LiteralType literalType, Token value) {
        this.literalType = literalType;
        this.value = value;
    }

    @Override
    public String toString() {
        return "LiteralExpr{" +
                "literalType=" + literalType +
                ", value=" + value +
                '}';
    }
}
