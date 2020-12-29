package Stmts;

import Expr.Ident;
import Expr.TyDef;

import java.util.ArrayList;

public class FuncStmt {

    private Ident funcName;
    private ArrayList<FuncParam> funcParams;
    private TyDef retType;
    private BlockStmt funcBody;

    public Ident getFuncName() {
        return funcName;
    }

    public ArrayList<FuncParam> getFuncParams() {
        return funcParams;
    }

    public TyDef getRetType() {
        return retType;
    }

    public BlockStmt getFuncBody() {
        return funcBody;
    }

    public FuncStmt(Ident funcName, ArrayList<FuncParam> funcParams, TyDef retType, BlockStmt funcBody) {
        this.funcName = funcName;
        this.funcParams = funcParams;
        this.retType = retType;
        this.funcBody = funcBody;
    }

    @Override
    public String toString() {
        return "FuncStmt{" +
                "funcName=" + funcName +
                ", funcParams=" + funcParams +
                ", retType=" + retType +
                ", funcBody=" + funcBody +
                '}';
    }
}
