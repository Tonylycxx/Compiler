package error;

public class FuncOrIdentDoNotExistsError extends Exception {

    private String noneFuncOrIdentName;

    public FuncOrIdentDoNotExistsError(String name) {
        noneFuncOrIdentName = name;
    }

    @Override
    public String toString() {
        return "FuncOrIdentDoNotExistsError: {" +
                "noneFuncOrIdentName='" + noneFuncOrIdentName + '\'' +
                '}';
    }
}
