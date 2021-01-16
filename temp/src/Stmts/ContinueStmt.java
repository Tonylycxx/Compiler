package Stmts;

public class ContinueStmt implements Stmt {

    @Override
    public String toString() {
        return "ContinueStmt{}";
    }

    @Override
    public String getStmtTy() {
        return "Continue";
    }
}
