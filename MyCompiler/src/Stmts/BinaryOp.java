package Stmts;

import tokenizer.TokenType;

public class BinaryOp {

    TokenType opType;

    public TokenType getOpType() {
        return opType;
    }

    public BinaryOp(TokenType opType) {
        this.opType = opType;
    }

    @Override
    public String toString() {
        return "BinaryOp{" +
                "opType=" + opType +
                '}';
    }
}
