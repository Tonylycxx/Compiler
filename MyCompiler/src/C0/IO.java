package C0;

import RawC0.FnDef;
import RawC0.GlobalValue;
import RawC0.Ins;
import util.HandleBytes;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class IO {

    public FileOutputStream stream;

    public IO(FileOutputStream stream) {
        this.stream = stream;
    }

    private void writeU32OrI32(int w) throws Exception {
        var bytes = HandleBytes.handleInt(w);
        for (byte b : bytes) {
            stream.write(b);
        }
    }

    private void writeU64OrI64(long l) throws Exception {
        var bytes = HandleBytes.handleLong(l);
        for (byte b : bytes) {
            stream.write(b);
        }
    }

    private void writeU64OrI64(double l) throws Exception {
        var bytes = HandleBytes.handleDouble(l);
        for (byte b : bytes) {
            stream.write(b);
        }
    }

    private void writeU8OrI8(byte b) throws Exception {
        stream.write(b);
    }

    public void writeMagicNumber(int magic) throws Exception {
        this.writeU32OrI32(magic);
    }

    public void writeVersion(int version) throws Exception {
        this.writeU32OrI32(version);
    }

    public void writeGlobals(ArrayList<GlobalValue> globals) throws Exception {
        if (globals.size() != 0) {
            this.writeU32OrI32(globals.size());
            for (GlobalValue globalValue : globals) {
                this.writeGlobalValue(globalValue);
            }
        }
    }

    private void writeGlobalValue(GlobalValue globalValue) throws Exception {
        if (globalValue.isConst)
            this.writeU8OrI8((byte) 1);
        else
            this.writeU8OrI8((byte) 0);
        this.writeU32OrI32(globalValue.bytes.size());
        for (int i = 0; i < globalValue.bytes.size(); i++)
            stream.write(globalValue.bytes.get(i));
    }

    public void writeFuncs(ArrayList<FnDef> functions) throws Exception {
        if (functions.size() != 0) {
            this.writeU32OrI32(functions.size());
            for (FnDef fnDef : functions)
                this.writeFndef(fnDef);
        }
    }

    private void writeFndef(FnDef fnDef) throws Exception {
        this.writeU32OrI32(fnDef.index);
        this.writeU32OrI32(fnDef.retSlots);
        this.writeU32OrI32(fnDef.paramSlots);
        this.writeU32OrI32(fnDef.locSlots);
        if (fnDef.instructions.size() != 0) {
            this.writeU32OrI32(fnDef.instructions.size());
            for (Ins ins : fnDef.instructions)
                this.writeIns(ins);
        }
    }

    private void writeIns(Ins ins) throws Exception {
        var op = ins.ins.getPos();
        var param = ins.getParam();
        this.writeU8OrI8(op.optnum);
        if (param != null) {
            if (ins.getParamLen(op.optnum) == 8) {
                if (param.getClass() == Long.class) {
                    this.writeU64OrI64((long) param);
                } else if (param.getClass() == Double.class) {
                    this.writeU64OrI64((double) param);
                } else if (param.getClass() == String.class) {
                    var c = ((String) param).charAt(0);
                    this.writeU64OrI64((long) c);
                } else {
                    throw new Exception("Cannot reach here! Invalid U64 Type!");
                }
            } else if (ins.getParamLen(op.optnum) == 4) {
                this.writeU32OrI32((int) param);
            }
        }
    }

}
