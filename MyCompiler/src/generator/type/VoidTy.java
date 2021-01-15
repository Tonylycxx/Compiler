package generator.type;

public class VoidTy implements Ty {

    public Types type = Types.Void;

    @Override
    public Types getType() {
        return type;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public FuncTy getFunc() {
        return null;
    }
}
