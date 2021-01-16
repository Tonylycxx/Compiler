package RawC0;

import java.util.ArrayList;

public class GlobalValue {

    public boolean isConst;
    public ArrayList<Byte> bytes;

    public GlobalValue(boolean isConst, ArrayList<Byte> bytes) {
        this.isConst = isConst;
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "GlobalValue{" +
                "isConst=" + isConst +
                ", bytes=" + bytes +
                '}';
    }
}
