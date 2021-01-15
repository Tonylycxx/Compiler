package Stmts;

public class IfElseIf implements Stmt {

    private IfStmt elseIf;

    public IfStmt getElseIf() {
        return elseIf;
    }

    public IfElseIf(IfStmt elseIf) {
        this.elseIf = elseIf;
    }

    @Override
    public String toString() {
        return "IfElseIf{" +
                "elseIf=" + elseIf +
                '}';
    }

    @Override
    public String getStmtTy() {
        return "ElseIf";
    }
}
