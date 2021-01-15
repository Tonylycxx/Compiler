package generator.type;

public class DoubleTy implements Ty {

    public Types type = Types.Double;

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
