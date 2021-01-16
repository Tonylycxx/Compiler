package Stmts;

import Expr.Ident;
import Expr.TyDef;

public class FuncParam {

    private boolean isConst;
    private Ident identName;
    private TyDef identType;

    public FuncParam(boolean isConst, Ident identName, TyDef identType) {
        this.isConst = isConst;
        this.identName = identName;
        this.identType = identType;
    }

    public boolean isConst() {
        return isConst;
    }

    public Ident getIdentName() {
        return identName;
    }

    public TyDef getIdentType() {
        return identType;
    }

    @Override
    public String toString() {
        return "FuncParam{" +
                "isConst=" + isConst +
                ", identName=" + identName +
                ", identType=" + identType +
                '}';
    }
}
