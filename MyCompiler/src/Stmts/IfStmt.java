package Stmts;

import Expr.Expr;

public class IfStmt implements Stmt {

    private Expr cond;
    private BlockStmt ifBlock;
    private Stmt elseEle;

    public IfStmt(Expr cond, BlockStmt ifBlock, Stmt elseEle) {
        this.cond = cond;
        this.ifBlock = ifBlock;
        this.elseEle = elseEle;
    }

    public Expr getCond() {
        return cond;
    }

    public BlockStmt getIfBlock() {
        return ifBlock;
    }

    public Stmt getElseEle() {
        return elseEle;
    }

    @Override
    public String toString() {
        return "IfStmt{" +
                "cond=" + cond +
                ", ifBlock=" + ifBlock +
                ", elseEle=" + elseEle +
                '}';
    }
}
