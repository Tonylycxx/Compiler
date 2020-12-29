package Expr;

import java.util.ArrayList;

public class CallExpr implements Expr {

    private Ident funcName;
    private ArrayList<Expr> params;

    public Ident getFuncName() {
        return funcName;
    }

    public ArrayList<Expr> getParams() {
        return params;
    }

    public CallExpr(Ident funcName, ArrayList<Expr> params) {
        this.funcName = funcName;
        this.params = params;
    }

    @Override
    public String toString() {
        return "CallExpr{" +
                "funcName=" + funcName +
                ", params=" + params +
                '}';
    }
}
