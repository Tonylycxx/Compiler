package Stmts;

public class EmptyStmt implements Stmt {

    @Override
    public String toString() {
        return "EmptyStmt{}";
    }

    @Override
    public String getStmtTy() {
        return "Empty";
    }
}
