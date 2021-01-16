package generator;

import generator.type.Ty;

public class Symbol {

    public int id;
    public Ty ty;
    public boolean isConst;

    public Symbol(Ty ty, boolean isConst) {
        this.id = 0;
        this.ty = ty;
        this.isConst = isConst;
    }

}
