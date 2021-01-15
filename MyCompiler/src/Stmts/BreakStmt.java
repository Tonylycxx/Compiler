package Stmts;

public class BreakStmt implements Stmt {

    @Override
    public String toString() {
        return "BreakStmt{}";
    }

    @Override
    public String getStmtTy() {
        return "Break";
    }
}
