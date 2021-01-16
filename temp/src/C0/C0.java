package C0;

import RawC0.B0;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class C0 {

    public B0 b0;
    public String writePath;

    public C0(B0 b0, String writePath) {
        this.b0 = b0;
        this.writePath = writePath;
    }

    public void writeCodeToFile() throws Exception {
        File targetFile = new File(writePath);
        FileOutputStream stream = new FileOutputStream(targetFile);
        IO io = new IO(stream);
        io.writeMagicNumber(0x72303b3e);
        io.writeVersion(1);
        io.writeGlobals(b0.globles);
        io.writeFuncs(b0.functions);
    }

}
