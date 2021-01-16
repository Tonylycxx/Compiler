package generator.type;

public class AddrTy implements Ty {

    public Types type = Types.Addr;

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
