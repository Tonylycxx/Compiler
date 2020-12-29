package Stmts;

import java.util.ArrayList;

public class BlockStmt implements Stmt {

    private ArrayList<Stmt> blockStmts;

    public ArrayList<Stmt> getBlockStmts() {
        return blockStmts;
    }

    public BlockStmt(ArrayList<Stmt> blockStmts) {
        this.blockStmts = blockStmts;
    }

    @Override
    public String toString() {
        return "BlockStmt{" +
                "blockStmts=" + blockStmts +
                '}';
    }
}
