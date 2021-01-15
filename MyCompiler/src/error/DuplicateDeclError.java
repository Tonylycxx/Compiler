package error;

public class DuplicateDeclError extends Exception {

    private String dupilcatedIdent;

    public DuplicateDeclError(String ident) {
        dupilcatedIdent = ident;
    }

    @Override
    public String toString() {
        return "DuplicateDeclError: {" +
                dupilcatedIdent +
                '}';
    }
}
