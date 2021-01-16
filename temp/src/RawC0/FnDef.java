package RawC0;

import java.util.ArrayList;

public class FnDef {

    public int index;
    public int retSlots;
    public int paramSlots;
    public int locSlots;
    public ArrayList<Ins> instructions;

    public FnDef(int index, int retSlots, int paramSlots, int locSlots, ArrayList<Ins> instructions) {
        this.index = index;
        this.retSlots = retSlots;
        this.paramSlots = paramSlots;
        this.locSlots = locSlots;
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "FnDef{" + '\n' +
                "    index=" + index + '\n' +
                "    retSlots=" + retSlots + '\n' +
                "    paramSlots=" + paramSlots + '\n' +
                "    locSlots=" + locSlots + '\n' +
                "    instructions=" + instructions + '\n' +
                '}';
    }
}
