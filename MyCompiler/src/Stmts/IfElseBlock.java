package Stmts;

public class IfElseBlock implements Stmt {

    private BlockStmt elseBlock;

    public BlockStmt getElseBlock() {
        return elseBlock;
    }

    public IfElseBlock(BlockStmt elseBlock) {
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString() {
        return "IfElseBlock{" +
                "elseBlock=" + elseBlock +
                '}';
    }

    @Override
    public String getStmtTy() {
        return "ElseBlock";
    }
}
