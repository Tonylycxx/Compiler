package Expr;

public class Ident implements Expr {

    private String identName;

    public String getIdentName() {
        return identName;
    }

    public Ident(String identName) {
        this.identName = identName;
    }

    @Override
    public String toString() {
        return "Ident{" +
                "identName='" + identName + '\'' +
                '}';
    }
}
