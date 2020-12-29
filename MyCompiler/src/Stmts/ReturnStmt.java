package Stmts;

import Expr.Expr;

public class ReturnStmt implements Stmt {

    private Expr retExpr;

    public Expr getRetExpr() {
        return retExpr;
    }

    public ReturnStmt(Expr retExpr) {
        this.retExpr = retExpr;
    }

    @Override
    public String toString() {
        return "ReturnStmt{" +
                "retExpr=" + retExpr +
                '}';
    }
}
