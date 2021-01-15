package generator;

import Expr.*;
import Expr.Ident;
import RawC0.FnDef;
import RawC0.Ins;
import RawC0.Op;
import Stmts.*;
import generator.type.*;
import tokenizer.Token;
import tokenizer.TokenType;
import util.TwoTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class FuncCodeGenerator {

    public FuncStmt func;
    public Scope globalScope;
    public GlobalEntry globalEntries;
    public ArrayList<BB> BBs;
    public Stack<TwoTuple<Integer, Integer>> BCPositions;
    public HashMap<Integer, Place> placeMapper;
    public int argTop;
    public int locTop;

    public FuncCodeGenerator(FuncStmt func, Scope scope, GlobalEntry globalEntries) {
        this.func = func;
        this.globalScope = scope;
        this.globalEntries = globalEntries;
        this.BBs = new ArrayList<>();
        this.BCPositions = new Stack<>();
        this.placeMapper = new HashMap<>();
        this.argTop = this.locTop = 0;
    }

    public FnDef complie() throws Exception {
        return compileFunc();
    }

    private FnDef compileFunc() throws Exception {
        var scope = new Scope(this.globalScope);
        var paramsAndRet = addParmas(scope);
        var startBB = newBB();
        var endBB = compileBlockWithoutScope(func.getFuncBody(), startBB, scope);
        if (scope.findIdent("$ret").ty.getType() == Types.Void) {
            setJump(endBB, new JumpInst("Return"));
        }
        var arrange = BBArrange(startBB);

        int loc = 0;
        int acc = 0;
        ArrayList<Integer> startOffset = new ArrayList<>();
        for (int i = 0; i < arrange.size(); i++)
            startOffset.add(null);
        for (int bb : arrange) {
            startOffset.set(loc, acc);
            acc += BBs.get(bb).code.size();
            if (BBs.get(bb).jump == null) {
            } else if (BBs.get(bb).jump.jumpTy.equals("Jump")) {
                acc += 1;
            } else if (BBs.get(bb).jump.jumpTy.equals("JumpIf")) {
                acc += 2;
            } else if (BBs.get(bb).jump.jumpTy.equals("Return")) {
                acc += 1;
            } else if (BBs.get(bb).jump.jumpTy.equals("Unreachable")) {
            } else {
                throw new Exception("Cannot reach here!");
            }
            loc++;
        }

        ArrayList<Ins> resCode = new ArrayList<>();
        for (int bb : arrange) {
            var BB = BBs.get(bb);
            resCode.addAll(BB.code);
            if (BB.jump.jumpTy.equals("Return")) {
                resCode.add(new Ins(new TwoTuple<>(Op.ret, null)));
            } else if (BB.jump == null) {
            } else if (BB.jump.jumpTy.equals("Jump")) {
                var index = BB.jump.ifTrueJump;
                resCode.add(new Ins(new TwoTuple<>(Op.br, startOffset.get(index) - resCode.size() - 1)));
            } else if (BB.jump.jumpTy.equals("JumpIf")) {
                var indexTrue = BB.jump.ifTrueJump;
                var indexFalse = BB.jump.ifFalseJump;
                resCode.add(new Ins(new TwoTuple<>(Op.brtrue, startOffset.get(indexTrue) - resCode.size() - 1)));
                resCode.add(new Ins(new TwoTuple<>(Op.brfalse, startOffset.get(indexFalse) - resCode.size() - 1)));
            } else {
            }
        }

        var nameValId = globalScope.getNextId();
        var nameGlobalId = globalEntries.insertStringLiteral(func.getFuncName().getIdentName(), nameValId);

        return new FnDef(nameGlobalId, paramsAndRet.getPos(), paramsAndRet.getVal(), locTop, resCode);

    }

    private ArrayList<Integer> BBArrange(int startBB) throws Exception {
        var BBArranger = new BBArranger(BBs);
        BBArranger.buildArrangement(startBB);
        return BBArranger.arrange();
    }

    private int newBB() {
        var bbId = BBs.size();
        BBs.add(new BB());
        return bbId;
    }

    private int compileBlockWithoutScope(BlockStmt funcBody, int startBB, Scope scope) throws Exception {
        var curBBId = startBB;
        for (Stmt stmt : funcBody.getBlockStmts()) {
            curBBId = compileStmt(stmt, curBBId, scope);
        }
        return curBBId;
    }

    private int compileStmt(Stmt stmt, int curBBId, Scope scope) throws Exception {
        if (stmt.getStmtTy().equals("Block")) {
            return compileBlock((BlockStmt) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("While")) {
            return compileWhile((WhileStmt) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("If")) {
            return compileIf((IfStmt) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("Expr")) {
            return compileExprStmt((Expr) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("Decl")) {
            return compileDecl((DeclStmt) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("Return")) {
            return compileReturn((ReturnStmt) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("Break")) {
            return compileBreak((BreakStmt) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("Continue")) {
            return compileContinue((ContinueStmt) stmt, curBBId, scope);
        } else if (stmt.getStmtTy().equals("Empty")) {
            return curBBId;
        } else {
            throw new Exception("Cannot reach here!");
        }
    }

    private int compileBlock(BlockStmt stmt, int curBBId, Scope scope) throws Exception {
        var blockScope = new Scope(scope);
        return compileBlockWithoutScope(stmt, curBBId, blockScope);
    }

    private int compileWhile(WhileStmt stmt, int curBBId, Scope scope) throws Exception {
        var condBB = newBB();
        var bodyBB = newBB();
        var nextBB = newBB();

        BCPositions.push(new TwoTuple<>(condBB, nextBB));

        compileExpr(stmt.getWhileCond(), condBB, scope);
        setJump(curBBId, new JumpInst("Jump", condBB));
        setJump(condBB, new JumpInst("JumpIf", bodyBB, nextBB));

        var bodyEndBB = compileBlock(stmt.getWhileBlock(), bodyBB, scope);
        setJump(bodyEndBB, new JumpInst("Jump", condBB));

        BCPositions.pop();
        return nextBB;
    }

    private void setJump(int bbID, JumpInst jumpInst) throws Exception {
        var BB = BBs.get(bbID);
        if (BB.jump == null) {
            BB.jump = jumpInst;
        } else {
            throw new Exception("Double set jump instruction!");
        }
    }

    private int compileIf(IfStmt stmt, int curBBId, Scope scope) throws Exception {
        var endBB = newBB();
        compileExpr(stmt.getCond(), curBBId, scope);

        var bbTrue = newBB();
        var bbTrueEnd = compileBlock(stmt.getIfBlock(), bbTrue, scope);

        setJump(bbTrueEnd, new JumpInst("Jump", endBB));

        if (stmt.getElseEle() == null) {
            var bbFalse = endBB;
            setJump(curBBId, new JumpInst("JumpIf", bbTrue, bbFalse));
            return endBB;
        } else if (stmt.getElseEle().getStmtTy().equals("ElseBlock")) {
            var bbFalse = newBB();
            var elseEnd = compileBlock((BlockStmt) stmt.getElseEle(), bbFalse, scope);
            setJump(elseEnd, new JumpInst("Jump", endBB));
            setJump(curBBId, new JumpInst("JumpIf", bbTrue, bbFalse));
            return endBB;
        } else if (stmt.getElseEle().getStmtTy().equals("ElseIf")) {
            var bbFalse = newBB();
            var elseEnd = compileIf((IfStmt) stmt.getElseEle(), bbFalse, scope);
            setJump(elseEnd, new JumpInst("Jump", endBB));
            setJump(curBBId, new JumpInst("JumpIf", bbTrue, bbFalse));
            return endBB;
        } else {
            throw new Exception("Error, cannot reach there!");
        }
    }

    private int compileExprStmt(Expr expr, int curBBId, Scope scope) throws Exception {
        var ty = compileExpr(expr, curBBId, scope);
        if (ty.getSize() > 0) {
            appendCode(curBBId, new Ins(new TwoTuple<>(Op.popn, ty.getSize())));
        }
        return curBBId;
    }

    private void appendCode(int curBBId, Ins ins) throws Exception {
        var BB = BBs.get(curBBId);
        if (BB != null) {
            BB.code.add(ins);
        } else {
            throw new Exception("BB doesn't exist!");
        }
    }

    private Ty compileExpr(Expr expr, int curBBId, Scope scope) throws Exception {
        if (expr.getExprTy().equals("Ident")) {
            return compileIdentExpr((Ident) expr, curBBId, scope);
        } else if (expr.getExprTy().equals("Assgin")) {
            return compileAssignExpr((AssignExpr) expr, curBBId, scope);
        } else if (expr.getExprTy().equals("As")) {
            return compileAsExpr((AsExpr) expr, curBBId, scope);
        } else if (expr.getExprTy().equals("Literal")) {
            return compileLiteralExpr((LiteralExpr) expr, curBBId, scope);
        } else if (expr.getExprTy().equals("Unary")) {
            return compileUnaryExpr((UnaryExpr) expr, curBBId, scope);
        } else if (expr.getExprTy().equals("Binary")) {
            return compileBinaryExpr((BinaryExpr) expr, curBBId, scope);
        } else if (expr.getExprTy().equals("Call")) {
            return compileCallExpr((CallExpr) expr, curBBId, scope);
        } else {
            throw new Exception("Error, cannot reach here!");
        }
    }

    private Ty compileIdentExpr(Ident expr, int curBBId, Scope scope) throws Exception {
        var identInfo = genIdentAddr(expr, curBBId, scope);
        appendCode(curBBId, opLoadTy(identInfo.getPos()));
        return identInfo.getPos();
    }

    private Ins opLoadTy(Ty ty) throws Exception {
        if (ty.getType() == Types.Int) {
            return new Ins(new TwoTuple<>(Op.load64, null));
        } else if (ty.getType() == Types.Double) {
            return new Ins(new TwoTuple<>(Op.load64, null));
        } else if (ty.getType() == Types.Bool) {
            return new Ins(new TwoTuple<>(Op.load64, null));
        } else if (ty.getType() == Types.Addr) {
            return new Ins(new TwoTuple<>(Op.load64, null));
        } else if (ty.getType() == Types.Void) {
            return new Ins(new TwoTuple<>(Op.pop, null));
        } else if (ty.getType() == Types.Func) {
            throw new Exception("Cannot be a function!");
        } else {
            throw new Exception("Error, cannot reach here!");
        }
    }

    private TwoTuple<Ty, Boolean> genIdentAddr(Ident expr, int curBBId, Scope scope) throws Exception {
        var symbolInfo = scope.findIsGlobal(expr.getIdentName());
        if (symbolInfo == null)
            throw new Exception("NoSuchSymbol!" + expr.getIdentName());
        if (symbolInfo.getVal()) {
            var globalVatId = globalEntries.getVarIndex(symbolInfo.getPos().id);
            appendCode(curBBId, new Ins(new TwoTuple<>(Op.globa, globalVatId)));
        } else {
            var varId = symbolInfo.getPos().id;
            appendCode(curBBId, opLoadAddr(getPlace(varId)));
        }
        return new TwoTuple<>(symbolInfo.getPos().ty, symbolInfo.getPos().isConst);
    }

    private Place getPlace(int varId) {
        return placeMapper.get(varId);
    }

    private Ty compileAssignExpr(AssignExpr expr, int curBBId, Scope scope) throws Exception {
        var lhsInfo = getLValueAddr(expr.getLhs(), curBBId, scope);
        var rhsTy = compileExpr(expr.getRhs(), curBBId, scope);
        checkTypeEqOrNot(lhsInfo.getPos(), rhsTy);
        if (!expr.isCanAssignConst() && lhsInfo.getVal()) {
            throw new Exception("Can not assign to a const value");
        } else {
            appendCode(curBBId, storeTy(lhsInfo.getPos()));
            return new VoidTy();
        }
    }

    private void checkTypeEqOrNot(Ty lhsTy, Ty rhsTy) throws Exception {
        if (lhsTy.getType() != rhsTy.getType()) {
            throw new Exception("Ty hasn't matched");
        } else {
            return;
        }
    }

    private TwoTuple<Ty, Boolean> getLValueAddr(Expr expr, int curBBId, Scope scope) throws Exception {
        if (expr.getExprTy().equals("Ident")) {
            return genIdentAddr((Ident) expr, curBBId, scope);
        } else {
            throw new Exception("Not a correct left value");
        }
    }

    private Ty compileAsExpr(AsExpr expr, int curBBId, Scope scope) throws Exception {
        var lhsTy = compileExpr(expr.getExpr(), curBBId, scope);
        var rhsTy = getTyNonVoid(expr.getExprType());
        var code = asExprIns(lhsTy, rhsTy);
        if (code == null)
            throw new Exception("InvalidAsExpr!");
        appendCode(curBBId, code);
        return rhsTy;
    }

    private Ins asExprIns(Ty fromTy, Ty toTy) throws Exception {
        if (fromTy.getType() == Types.Int || fromTy.getType() == Types.Addr) {
            if (toTy.getType() == Types.Double) {
                return new Ins(new TwoTuple<>(Op.itof, null));
            }
            return null;
        } else if (fromTy.getType() == Types.Double) {
            if (toTy.getType() == Types.Int) {
                return new Ins(new TwoTuple<>(Op.ftoi, null));
            }
            return null;
        } else if (fromTy.getType() == Types.Bool || fromTy.getType() == Types.Func || fromTy.getType() == Types.Void) {
            return null;
        } else {
            throw new Exception("Error, cannot reach here!");
        }
    }

    private Ty getTyNonVoid(TyDef tyDef) throws Exception {
        if (tyDef.getTypeName().equals("Int")) {
            return new IntTy();
        } else if (tyDef.getTypeName().equals("double")) {
            return new DoubleTy();
        } else if (tyDef.getTypeName().equals("void")) {
            throw new Exception("VoidTypeVariable");
        } else {
            throw new Exception("Unknown type, cannot reach here!");
        }
    }

    private Ty compileLiteralExpr(LiteralExpr expr, int curBBId, Scope scope) throws Exception {
        if (expr.getLiteralType() == LiteralType.Integer) {
            appendCode(curBBId, new Ins(new TwoTuple<>(Op.push, expr.getValue().getValue())));
            return new IntTy();
        } else if (expr.getLiteralType() == LiteralType.Float) {
            appendCode(curBBId, new Ins(new TwoTuple<>(Op.push, expr.getValue().getValue())));
            return new DoubleTy();
        } else if (expr.getLiteralType() == LiteralType.String) {
            var valID = scope.getNextId();
            var globalID = globalEntries.insertStringLiteral(expr.getValue().getValueString(), valID);
            appendCode(curBBId, new Ins(new TwoTuple<>(Op.push, globalID)));
            return new IntTy();
        } else {
            throw new Exception("InvalidLiteralType!");
        }
    }

    private Ty compileUnaryExpr(UnaryExpr expr, int curBBId, Scope scope) throws Exception {
        var lhsTy = compileExpr(expr.getExpr(), curBBId, scope);
        var code = unaryOpOp(expr.getUnaryOp(), lhsTy);
        if (code != null)
            appendCode(curBBId, code);
        return lhsTy;
    }

    private Ins unaryOpOp(TokenType unaryOp, Ty lhsTy) throws Exception {
        if (unaryOp == TokenType.Minus) {
            if (lhsTy.getType() == Types.Int)
                return new Ins(new TwoTuple<>(Op.negi, null));
            else if (lhsTy.getType() == Types.Double)
                return new Ins(new TwoTuple<>(Op.negf, null));
            else
                throw new Exception("Cannot reach here!");
        } else {
            return null;
        }
    }

    private Ty compileBinaryExpr(BinaryExpr expr, int curBBId, Scope scope) throws Exception {
        var lhsTy = compileExpr(expr.getLhs(), curBBId, scope);
        var rhsTy = compileExpr(expr.getRhs(), curBBId, scope);
        checkTypeEqOrNot(lhsTy, rhsTy);
        var code = binaryOpOp(expr.getBinaryOp(), lhsTy);
        for (Ins ins : code) {
            appendCode(curBBId, ins);
        }
        var resTy = binaryOpResTy(expr.getBinaryOp(), lhsTy);
        return resTy;
    }

    private Ty binaryOpResTy(TokenType binaryOp, Ty lhsTy) throws Exception {
        if (lhsTy.getType() == Types.Int || lhsTy.getType() == Types.Double || lhsTy.getType() == Types.Addr) {
            if (binaryOp == TokenType.Plus || binaryOp == TokenType.Minus || binaryOp == TokenType.Mul || binaryOp == TokenType.Div) {
                return deterRetTy(lhsTy);
            } else if (binaryOp == TokenType.Gt || binaryOp == TokenType.Lt || binaryOp == TokenType.Ge || binaryOp == TokenType.Le || binaryOp == TokenType.Eq || binaryOp == TokenType.Neq) {
                return new BoolTy();
            } else {
                throw new Exception("Cannot reach here, invalid binaryOp Ty!");
            }
        } else {
            throw new Exception("Cannot reach here, Invalid binaryExpr Ty!");
        }
    }

    private Ty deterRetTy(Ty lhsTy) throws Exception {
        if (lhsTy.getType() == Types.Int) {
            return new IntTy();
        } else if (lhsTy.getType() == Types.Double) {
            return new DoubleTy();
        } else if (lhsTy.getType() == Types.Addr) {
            return new AddrTy();
        } else {
            throw new Exception("Cannot reach here!");
        }
    }

    private ArrayList<Ins> binaryOpOp(TokenType binaryOp, Ty lhsTy) throws Exception {
        ArrayList<Ins> res = new ArrayList();
        if (lhsTy.getType() == Types.Int || lhsTy.getType() == Types.Addr) {
            if (binaryOp == TokenType.Plus) {
                res.add(new Ins(new TwoTuple<>(Op.addi, null)));
            } else if (binaryOp == TokenType.Minus) {
                res.add(new Ins(new TwoTuple<>(Op.subi, null)));
            } else if (binaryOp == TokenType.Mul) {
                res.add(new Ins(new TwoTuple<>(Op.muli, null)));
            } else if (binaryOp == TokenType.Gt) {
                res.add(new Ins(new TwoTuple<>(Op.cmpi, null)));
                res.add(new Ins(new TwoTuple<>(Op.setgt, null)));
            } else if (binaryOp == TokenType.Lt) {
                res.add(new Ins(new TwoTuple<>(Op.cmpi, null)));
                res.add(new Ins(new TwoTuple<>(Op.setlt, null)));
            } else if (binaryOp == TokenType.Ge) {
                res.add(new Ins(new TwoTuple<>(Op.cmpi, null)));
                res.add(new Ins(new TwoTuple<>(Op.setlt, null)));
                res.add(new Ins(new TwoTuple<>(Op.not, null)));
            } else if (binaryOp == TokenType.Le) {
                res.add(new Ins(new TwoTuple<>(Op.cmpi, null)));
                res.add(new Ins(new TwoTuple<>(Op.setgt, null)));
                res.add(new Ins(new TwoTuple<>(Op.not, null)));
            } else if (binaryOp == TokenType.Eq) {
                res.add(new Ins(new TwoTuple<>(Op.cmpi, null)));
                res.add(new Ins(new TwoTuple<>(Op.not, null)));
            } else if (binaryOp == TokenType.Neq) {
                res.add(new Ins(new TwoTuple<>(Op.cmpi, null)));
            } else {
                throw new Exception("Cannot reach here, invalid binary Op!");
            }
            return res;
        } else if (lhsTy.getType() == Types.Double) {
            if (binaryOp == TokenType.Plus) {
                res.add(new Ins(new TwoTuple<>(Op.addf, null)));
            } else if (binaryOp == TokenType.Minus) {
                res.add(new Ins(new TwoTuple<>(Op.subf, null)));
            } else if (binaryOp == TokenType.Mul) {
                res.add(new Ins(new TwoTuple<>(Op.mulf, null)));
            } else if (binaryOp == TokenType.Gt) {
                res.add(new Ins(new TwoTuple<>(Op.cmpf, null)));
                res.add(new Ins(new TwoTuple<>(Op.setgt, null)));
            } else if (binaryOp == TokenType.Lt) {
                res.add(new Ins(new TwoTuple<>(Op.cmpf, null)));
                res.add(new Ins(new TwoTuple<>(Op.setlt, null)));
            } else if (binaryOp == TokenType.Ge) {
                res.add(new Ins(new TwoTuple<>(Op.cmpf, null)));
                res.add(new Ins(new TwoTuple<>(Op.setlt, null)));
                res.add(new Ins(new TwoTuple<>(Op.not, null)));
            } else if (binaryOp == TokenType.Le) {
                res.add(new Ins(new TwoTuple<>(Op.cmpf, null)));
                res.add(new Ins(new TwoTuple<>(Op.setgt, null)));
                res.add(new Ins(new TwoTuple<>(Op.not, null)));
            } else if (binaryOp == TokenType.Eq) {
                res.add(new Ins(new TwoTuple<>(Op.cmpf, null)));
                res.add(new Ins(new TwoTuple<>(Op.not, null)));
            } else if (binaryOp == TokenType.Neq) {
                res.add(new Ins(new TwoTuple<>(Op.cmpf, null)));
            } else {
                throw new Exception("Cannot reach here, invalid binary Op!");
            }
            return res;
        } else {
            throw new Exception("Cannot reach here, invalid binary expr Ty!");
        }
    }

    private Ty compileCallExpr(CallExpr expr, int curBBId, Scope scope) throws Exception {
        ArrayList<Ty> exprTys = new ArrayList<>();
        var funcName = expr.getFuncName().getIdentName();
        var funcSig = globalScope.findIdent(funcName);
        if (funcSig == null)
            throw new Exception("NoSuchFunction!");
        var funcTy = funcSig.ty.getFunc();
        if (funcTy == null)
            throw new Exception("NotAFuncButAIdent!");
        appendCode(curBBId, new Ins(new TwoTuple<>(Op.stackalloc, funcTy.retType.getSize())));
        if (expr.getParams() != null) {
            for (Expr subExpr : expr.getParams()) {
                var ty = compileExpr(subExpr, curBBId, scope);
                exprTys.add(ty);
            }
        }
        if (exprTys.size() != funcTy.params.size())
            throw new Exception("FuncParamsSizeMismatch!");
        for (int i = 0; i < exprTys.size(); i++) {
            checkTypeEqOrNot(exprTys.get(i), funcTy.params.get(i));
        }
        var retTy = funcTy.retType;
        var funcID = globalEntries.getFuncIndex(funcName);
        if (funcID != -1) {
            appendCode(curBBId, new Ins(new TwoTuple<>(Op.call, funcID)));
        } else {
            var valID = scope.getNextId();
            var globalID = globalEntries.insertStringLiteral(funcName, valID);
            appendCode(curBBId, new Ins(new TwoTuple<>(Op.callname, globalID)));
        }
        return retTy;
    }

    private int compileDecl(DeclStmt stmt, int curBBId, Scope scope) throws Exception {
        var symbolInfo = Generator.addDeclToScope(stmt, scope);
        placeMapper.put(symbolInfo.getPos(), new Place(PlaceType.Loc, locTop));
        var varSize = symbolInfo.getVal().getSize();
        locTop += varSize;

        if (stmt.getValue() != null) {
            var assignExpr = new AssignExpr(stmt.getIdent(), stmt.getValue(), true);
            compileAssignExpr(assignExpr, curBBId, scope);
        }
        return curBBId;
    }

    private int compileReturn(ReturnStmt stmt, int curBBId, Scope scope) throws Exception {
        var funcTy = (FuncTy) globalScope.findIdent(func.getFuncName().getIdentName()).ty;
        var retTy = funcTy.retType;
        if (retTy.getType() == Types.Void) {
            if (stmt.getRetExpr() != null) {
                throw new Exception("Type Mismatch: " + "expect: void, " + "get: " + stmt.getRetExpr().getStmtTy());
            }
        } else {
            if (stmt.getRetExpr() == null) {
                throw new Exception("Type Mismatch: " + "expect: " + stmt.getRetExpr().getStmtTy() + ", " + "get: void");
            } else {
                var retID = scope.findIdent("$ret").id;
                var offset = placeMapper.get(retID);
                appendCode(curBBId, opLoadAddr(offset));
                var ty = compileExpr(stmt.getRetExpr(), curBBId, scope);
                if (ty.getType() != retTy.getType()) {
                    throw new Exception("Type Mismathch! " + "expect: " + retTy.getType() + " get: " + ty.getType());
                }
                appendCode(curBBId, storeTy(retTy));
            }
        }
        setJump(curBBId, new JumpInst("Return"));
        return newBB();
    }

    private Ins storeTy(Ty ty) throws Exception {
        switch (ty.getType()) {
            case Int:
            case Addr:
            case Double:
            case Bool:
                return new Ins(new TwoTuple<>(Op.store64, null));
            case Void:
                return new Ins(new TwoTuple<>(Op.pop, null));
            default:
                throw new Exception("Invalid type!");
        }
    }

    private Ins opLoadAddr(Place place) {
        if (place.placeType == PlaceType.Arg)
            return new Ins(new TwoTuple<>(Op.arga, place.add));
        else
            return new Ins(new TwoTuple<>(Op.loca, place.add));
    }

    private int compileBreak(BreakStmt stmt, int curBBId, Scope scope) throws Exception {
        var next = BCPositions.peek().getVal();
        setJump(curBBId, new JumpInst("Jump", next));
        return newBB();
    }

    private int compileContinue(ContinueStmt stmt, int curBBId, Scope scope) throws Exception {
        var next = BCPositions.peek().getPos();
        setJump(curBBId, new JumpInst("Jump", next));
        return newBB();
    }

    private TwoTuple<Integer, Integer> addParmas(Scope scope) throws Exception {
        var retTy = Generator.getStmtType(this.func.getRetType());
        var retSize = retTy.getSize();
        var retId = scope.insert("$ret", new Symbol(retTy, false));
        this.placeMapper.put(retId, new Place(PlaceType.Arg, argTop));
        argTop += retSize;

        if (func.getFuncParams() != null) {
            for (FuncParam param : func.getFuncParams()) {
                var paramTy = Generator.getNoVoidType(param);
                var paramSize = paramTy.getSize();

                var paramId = scope.insert(param.getIdentName().getIdentName(), new Symbol(paramTy, param.isConst()));
                placeMapper.put(paramId, new Place(PlaceType.Arg, argTop));
                argTop += paramSize;
            }
        }

        return new TwoTuple<>(retSize, argTop - retSize);
    }

}
