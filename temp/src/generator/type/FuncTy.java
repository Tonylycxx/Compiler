package generator.type;

import java.util.ArrayList;

public class FuncTy implements Ty {

    public Types type = Types.Func;
    public ArrayList<Ty> params;
    public Ty retType;

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
        return this;
    }

    public FuncTy(ArrayList<Ty> params, Ty retType) {
        this.params = params;
        this.retType = retType;
    }
}
