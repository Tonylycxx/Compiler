package util;

public class TwoTuple<P, V> {

    private P pos;
    private V val;

    public TwoTuple(P pos, V val) {
        this.pos = pos;
        this.val = val;
    }

    public P getPos() {
        return pos;
    }

    public V getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "TwoTuple{" +
                "pos=" + pos +
                ", val=" + val +
                '}';
    }
}
