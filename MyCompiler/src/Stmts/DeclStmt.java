package Stmts;

import Expr.Expr;
import Expr.Ident;
import Expr.TyDef;

public class DeclStmt implements Stmt {

    private boolean isConst;
    private Ident ident;
    private Expr value;
    private TyDef type;

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public Ident getIdent() {
        return ident;
    }

    public Expr getValue() {
        return value;
    }

    public TyDef getType() {
        return type;
    }

    public DeclStmt(boolean isConst, Ident ident, Expr value, TyDef type) {
        this.isConst = isConst;
        this.ident = ident;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "DeclStmt{" +
                "isConst=" + isConst +
                ", ident=" + ident +
                ", value=" + value +
                ", type=" + type +
                '}';
    }

    @Override
    public String getStmtTy() {
        return "Decl";
    }
}
