package generator.type;

public enum Types {

    Void,
    Func,
    Int,
    Double,
    Bool,
    Addr;

    @Override
    public String toString() {
        switch (this) {
            case Int:
                return "Int";
            case Double:
                return "Double";
            case Bool:
                return "Bool";
            case Addr:
                return "Addr";
            case Func:
                return "Func";
            case Void:
                return "Void";
            default:
                return "Error!";
        }
    }
}
