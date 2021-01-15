package error;

public class ParamIsVoidError extends Exception {

    private String message;

    public ParamIsVoidError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ParamIsVoidError{" +
                "message='" + message + '\'' +
                '}';
    }
}
