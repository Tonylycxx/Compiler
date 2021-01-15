package Expr;

public class TyDef implements Expr {

    private String typeName;

    public TyDef(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return "TyDef{" +
                "typeName='" + typeName + '\'' +
                '}';
    }

    @Override
    public String getStmtTy() {
        return "Expr";
    }

    @Override
    public String getExprTy() {
        return "TyDef";
    }
}
