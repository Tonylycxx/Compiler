package RawC0;

import util.TwoTuple;

public class Ins {

    public TwoTuple<Op, Object> ins;

    public Ins(TwoTuple<Op, Object> ins) {
        this.ins = ins;
    }

    public Object getParam() {
        return ins.getVal();
    }

    public int getParamLen(byte index) {
        switch (index) {
            case 0x01:
            case 0x40:
                return 8;
            case 0x03:
            case 0x0a:
            case 0x0b:
            case 0x0c:
            case 0x1a:
            case 0x41:
            case 0x42:
            case 0x43:
            case 0x48:
            case 0x4a:
                return 4;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return '\n' + "        Ins{" +
                "ins=" + ins +
                '}';
    }
}
