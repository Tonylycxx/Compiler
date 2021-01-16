package generator;

import RawC0.Ins;
import RawC0.Op;

import java.util.ArrayList;

public class BB {

    public ArrayList<Ins> code;
    public JumpInst jump;

    public BB() {
        this.code = new ArrayList<>();
        this.jump = null;
    }

    public BB(ArrayList<Ins> code, JumpInst jump) {
        this.code = code;
        this.jump = jump;
    }
}
