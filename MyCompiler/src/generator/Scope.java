package generator;

import error.DuplicateDeclError;
import util.TwoTuple;

import java.util.HashMap;

public class Scope {

    private SymbolIdGenerator symbolGenerator;
    private Scope parentScope;
    private HashMap<String, Symbol> vars;

    public Scope(SymbolIdGenerator symbolGenerator) {
        this.symbolGenerator = symbolGenerator;
        this.parentScope = null;
        vars = new HashMap<>();
    }

    public Scope(Scope parentScope) {
        this.symbolGenerator = parentScope.symbolGenerator;
        this.parentScope = parentScope;
        vars = new HashMap<>();
    }

    private Symbol findInSelf(String ident) {
        return vars.get(ident);
    }

    public Symbol findIdent(String ident) {
        Symbol res = this.findInSelf(ident);
        if (res == null) {
            if (parentScope != null) {
                return parentScope.findIdent(ident);
            } else {
                return null;
            }
        }
        return res;
    }

    public boolean isRootScope() {
        return this.parentScope == null;
    }

    public TwoTuple<Symbol, Boolean> findIsGlobal(String ident) {
        Symbol res = this.findInSelf(ident);
        if (res == null) {
            if(isRootScope()) {
                return null;
            }
            return parentScope.findIsGlobal(ident);
        } else {
            if (isRootScope()) {
                return new TwoTuple<>(res, true);
            } else {
                return new TwoTuple<>(res, false);
            }
        }
    }

    public int insert(String ident, Symbol identSymbol) throws DuplicateDeclError {
        Symbol ifExist = vars.get(ident);
        if (ifExist == null) {
            identSymbol.id = this.getNextId();
            vars.put(ident, identSymbol);
            return identSymbol.id;
        } else {
            throw new DuplicateDeclError(ident);
        }
    }

    public int getNextId() {
        return this.symbolGenerator.getNextId();
    }

}
