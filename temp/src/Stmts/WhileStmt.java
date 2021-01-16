package Stmts;

import Expr.Expr;

public class WhileStmt implements Stmt {

    private Expr whileCond;
    private BlockStmt whileBlock;

    public Expr getWhileCond() {
        return whileCond;
    }

    public BlockStmt getWhileBlock() {
        return whileBlock;
    }

    public WhileStmt(Expr whileCond, BlockStmt whileBlock) {
        this.whileCond = whileCond;
        this.whileBlock = whileBlock;
    }

    @Override
    public String toString() {
        return "WhileStmt{" +
                "whileCond=" + whileCond +
                ", whileBlock=" + whileBlock +
                '}';
    }

    @Override
    public String getStmtTy() {
        return "While";
    }
}
