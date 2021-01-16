package generator.type;

public class IntTy implements Ty {

    public Types type = Types.Int;

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
