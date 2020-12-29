package Stmts;

import java.util.ArrayList;

public class Program {

    private ArrayList<FuncStmt> funcs;
    private ArrayList<DeclStmt> decls;

    public Program(ArrayList<FuncStmt> funcs, ArrayList<DeclStmt> decls) {
        this.funcs = funcs;
        this.decls = decls;
    }

    @Override
    public String toString() {
        return "Program{" + '\n' +
                "funcs=" + funcs + '\n' +
                ", decls=" + decls + '\n' +
                '}';
    }
}
