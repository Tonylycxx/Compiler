package Expr;

public class AsExpr implements Expr {

    private TyDef exprType;
    private Expr expr;

    public TyDef getExprType() {
        return exprType;
    }

    public Expr getExpr() {
        return expr;
    }

    public AsExpr(TyDef exprType, Expr expr) {
        this.exprType = exprType;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "AsExpr{" +
                "exprType=" + exprType +
                ", expr=" + expr +
                '}';
    }
}
