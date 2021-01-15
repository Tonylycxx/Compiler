package generator;

public class SymbolIdGenerator {

    private int nextId;

    public SymbolIdGenerator() {
        this.nextId = 0;
    }

    public int getNextId() {
        int res = this.nextId;
        this.nextId++;
        return res;
    }

}
