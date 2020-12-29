package Expr;

public class AssignExpr implements Expr {

    private Expr lhs;
    private Expr rhs;
    private boolean canAssignConst;

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    public boolean isCanAssignConst() {
        return canAssignConst;
    }

    public AssignExpr(Expr lhs, Expr rhs, boolean canAssignConst) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.canAssignConst = canAssignConst;
    }

    @Override
    public String toString() {
        return "AssignExpr{" +
                "lhs=" + lhs +
                ", rhs=" + rhs +
                ", canAssignConst=" + canAssignConst +
                '}';
    }
}
