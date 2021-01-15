package RawC0;

import util.TwoTuple;

public class Ins {

    public TwoTuple<Op, Object> ins;

    public Ins(TwoTuple<Op, Object> ins) {
        this.ins = ins;
    }

    @Override
    public String toString() {
        return '\n' + "        Ins{" +
                "ins=" + ins +
                '}';
    }
}
