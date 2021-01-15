package generator;

import Expr.CallExpr;
import Expr.Ident;
import Expr.TyDef;
import RawC0.B0;
import RawC0.FnDef;
import RawC0.GlobalValue;
import Stmts.*;
import error.DuplicateDeclError;
import error.ParamIsVoidError;
import error.TypeNotExistsError;
import generator.type.*;
import util.TwoTuple;

import java.util.ArrayList;

public class Generator {

    public B0 compileProgram(Program program) throws Exception {
        var globalSymbolGenerator = new SymbolIdGenerator();
        var globalScope = new Scope(globalSymbolGenerator);
        var globalEntries = new GlobalEntry();

        ArrayList<FnDef> funcs = new ArrayList<>();
        createLibFuncs(globalScope);
        for (var decl : program.getDecls()) {
            var symbolInfo = addDeclToScope(decl, globalScope);
            globalEntries.vars.put(symbolInfo.getPos(), globalEntries.vars.getKeySetLength(), new ArrayList<Byte>());
        }
        globalEntries.functions.add("_start");
        for (var func : program.getFuncs()) {
            if (globalEntries.functions.contains(func.getFuncName().getIdentName()))
                throw new DuplicateDeclError("Func: " + func.getFuncName());
            else
                globalEntries.functions.add(func.getFuncName().getIdentName());
            var funcDef = compileFunc(func, globalScope, globalEntries);
            funcs.add(funcDef);
        }
        var start = compileStartFunc(program, globalScope, globalEntries);
        funcs.add(0, start);
        ArrayList<GlobalValue> resVars = new ArrayList();
        if (globalEntries.vars.size() != 0) {
            for (int i = 0; i < globalEntries.vars.size(); i++)
                resVars.add(null);
            for (TwoTuple<Integer, ArrayList<Byte>> varInfo : globalEntries.vars.getValueSet()) {
                resVars.set(varInfo.getPos(), new GlobalValue(false, varInfo.getVal()));
            }
        }
        return new B0(resVars, funcs);
    }

    private FnDef compileStartFunc(Program program, Scope globalScope, GlobalEntry globalEntries) throws Exception {
        ArrayList<FuncParam> params = new ArrayList<>();
        ArrayList<Stmt> stmts = new ArrayList<>();
        if (program.getDecls().size() != 0) {
            for (DeclStmt stmt : program.getDecls()) {
                DeclStmt tempStmt = stmt;
                tempStmt.setConst(true);
                stmts.add(tempStmt);
            }
        }
        CallExpr callMain = new CallExpr(new Ident("main"), new ArrayList<>());
        stmts.add(callMain);
        stmts.add(new ReturnStmt(null));
        BlockStmt blockStmt = new BlockStmt(stmts);
        var startFunc = new FuncStmt(new Ident("_start"), params, new TyDef("void"), blockStmt);
        var func = compileFunc(startFunc, globalScope, globalEntries);
        func.instructions.remove(func.instructions.size() - 1);
        return func;
    }

    private FnDef compileFunc(FuncStmt func, Scope scope, GlobalEntry globalEntries) throws Exception {
        var retTy = getStmtType(func.getRetType());
        var params = getFuncParams(func);
        var funTy = new FuncTy(params, retTy);
        var symbol = new Symbol(funTy, true);
        scope.insert(func.getFuncName().getIdentName(), symbol);
        globalEntries.functions.add(func.getFuncName().getIdentName());

        var funcCode = new FuncCodeGenerator(func, scope, globalEntries);
        return funcCode.complie();
    }

    private ArrayList<Ty> getFuncParams(FuncStmt func) throws Exception {
        ArrayList<Ty> params = new ArrayList<>();
        if (func.getFuncParams() == null)
            return params;
        for (FuncParam param : func.getFuncParams()) {
            var paramTy = getNoVoidType(param);
            params.add(paramTy);
        }
        return params;
    }

    public static Ty getNoVoidType(FuncParam param) throws Exception {
        if (param.getIdentType().getTypeName().equals("int")) {
            return new IntTy();
        } else if (param.getIdentType().getTypeName().equals("double")) {
            return new DoubleTy();
        } else if (param.getIdentType().getTypeName().equals("void")) {
            throw new ParamIsVoidError("Param: " + param.getIdentName().getIdentName() + " is void!");
        } else {
            throw new TypeNotExistsError("Type: " + param.getIdentType().getTypeName() + "doesn't exist!");
        }
    }

    private void createLibFuncs(Scope scope) throws Exception {
        {
            ArrayList<Ty> params = new ArrayList<>();
            params.add(new IntTy());
            var funcTy = new FuncTy(params, new VoidTy());
            var symbol = new Symbol(funcTy, true);
            scope.insert("putint", symbol);
        }
        {
            ArrayList<Ty> params = new ArrayList<>();
            params.add(new DoubleTy());
            var funcTy = new FuncTy(params, new VoidTy());
            var symbol = new Symbol(funcTy, true);
            scope.insert("putdouble", symbol);
        }
        {
            ArrayList<Ty> params = new ArrayList<>();
            params.add(new IntTy());
            var funcTy = new FuncTy(params, new VoidTy());
            var symbol = new Symbol(funcTy, true);
            scope.insert("putchar", symbol);
        }
        {
            ArrayList<Ty> params = new ArrayList<>();
            var funcTy = new FuncTy(params, new VoidTy());
            var symbol = new Symbol(funcTy, true);
            scope.insert("putln", symbol);
        }
        {
            ArrayList<Ty> params = new ArrayList<>();
            var funcTy = new FuncTy(params, new IntTy());
            var symbol = new Symbol(funcTy, true);
            scope.insert("getchar", symbol);
        }
        {
            ArrayList<Ty> params = new ArrayList<>();
            var funcTy = new FuncTy(params, new IntTy());
            var symbol = new Symbol(funcTy, true);
            scope.insert("getint", symbol);
        }
        {
            ArrayList<Ty> params = new ArrayList<>();
            var funcTy = new FuncTy(params, new DoubleTy());
            var symbol = new Symbol(funcTy, true);
            scope.insert("getdouble", symbol);
        }
    }

    public static TwoTuple<Integer, Ty> addDeclToScope(DeclStmt decl, Scope scope) throws Exception {
        var type = getStmtType(decl.getType());
        if (type.getType() == Types.Void) {
            throw new Exception("Expr's retval cannot be void");
        }
        var name = decl.getIdent().getIdentName();
        var symbol = new Symbol(type, decl.isConst());
        var symbolId = scope.insert(name, symbol);
        return new TwoTuple<>(symbolId, type);
    }

    public static Ty getStmtType(TyDef tyDef) throws TypeNotExistsError {
        var type = tyDef.getTypeName();
        if (type.equals("void")) {
            return new VoidTy();
        } else if (type.equals("int")) {
            return new IntTy();
        } else if (type.equals("double")) {
            return new DoubleTy();
        } else {
            throw new TypeNotExistsError("Type: " + type + "doesn't exist!");
        }
    }
}
