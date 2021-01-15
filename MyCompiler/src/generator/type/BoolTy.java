package generator.type;

public class BoolTy implements Ty {

    public Types type = Types.Bool;

    @Override
    public Types getType() {
        return type;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public FuncTy getFunc() {
        return null;
    }
}
