package Expr;

import tokenizer.Token;
import tokenizer.TokenType;

public class BinaryExpr implements Expr {

    private Expr lhs;
    private Expr rhs;
    private TokenType binaryOp;

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    public TokenType getBinaryOp() {
        return binaryOp;
    }

    public BinaryExpr(Expr lhs, Expr rhs, TokenType binaryOp) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.binaryOp = binaryOp;
    }

    @Override
    public String toString() {
        return "BinaryExpr{" +
                "lhs=" + lhs +
                ", rhs=" + rhs +
                ", binaryOp=" + binaryOp +
                '}';
    }

    @Override
    public String getStmtTy() {
        return "Expr";
    }

    @Override
    public String getExprTy() {
        return "Binary";
    }
}
