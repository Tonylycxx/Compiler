package error;

public class TypeNotExistsError extends Exception {

    private String message;

    public TypeNotExistsError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TypeNotExistsError: {" +
                "message='" + message + '\'' +
                '}';
    }
}
