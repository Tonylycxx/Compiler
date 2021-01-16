package generator;

import error.FuncOrIdentDoNotExistsError;
import util.HandleBytes;
import util.ThreeTuple;

import java.util.ArrayList;

public class GlobalEntry {

    public ArrayList<String> functions;
    public ThreeTuple<Integer, Integer, ArrayList<Byte>> vars;

    public GlobalEntry() {
        this.functions = new ArrayList<>();
        this.vars = new ThreeTuple();
    }

    public int getFuncIndex(String funName) {
        if(functions.contains(funName))
            return functions.indexOf(funName);
        else
            return -1;
    }

    public int getVarIndex(int symbolId) throws FuncOrIdentDoNotExistsError {
        if(vars.containsKey(symbolId))
            return vars.get(symbolId).getPos();
        else
            throw new FuncOrIdentDoNotExistsError("Ident Does not Exist!");
    }

    public int insertStringLiteral(String stringLiteral, int valPos) {
        ArrayList<Byte> stringInBytes = HandleBytes.handleString(stringLiteral);
        return vars.put(valPos, vars.getKeySetLength(), stringInBytes).getPos();
    }

}
