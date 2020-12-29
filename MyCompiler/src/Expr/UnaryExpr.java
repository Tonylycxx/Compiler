package Expr;

import tokenizer.TokenType;

public class UnaryExpr implements Expr {

    private TokenType unaryOp;
    private Expr expr;

    public TokenType getUnaryOp() {
        return unaryOp;
    }

    public Expr getExpr() {
        return expr;
    }

    public UnaryExpr(TokenType unaryOp, Expr expr) {
        this.unaryOp = unaryOp;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "UnaryExpr{" +
                "unaryOp=" + unaryOp +
                ", expr=" + expr +
                '}';
    }
}
