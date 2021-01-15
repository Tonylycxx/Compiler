package RawC0;

import java.util.ArrayList;

public class B0 {

    public ArrayList<GlobalValue> globles;
    public ArrayList<FnDef> functions;

    public B0(ArrayList<GlobalValue> globles, ArrayList<FnDef> functions) {
        this.globles = globles;
        this.functions = functions;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("");
        res.append("B0{" + '\n' + "Globals :" + '\n');
        for(int i = 0; i < globles.size(); i++)
            res.append(globles.get(i).toString() + '\n');
        res.append("Functions :" + '\n');
        for (int i = 0; i < functions.size(); i++)
            res.append(functions.get(i).toString() + '\n');
        res.append("}" + '\n');
        return res.toString();
    }
}
