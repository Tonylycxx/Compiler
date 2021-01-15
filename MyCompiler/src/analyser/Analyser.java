package analyser;

import Expr.*;
import Stmts.*;
import error.CompileError;
import error.ErrorCode;
import error.ExpectedTokenError;
import error.TokenizeError;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;
import util.Pos;

import java.util.*;

public final class Analyser {

    private Tokenizer tokenizer;
    private ArrayList<FuncStmt> funcs;
    private ArrayList<DeclStmt> decls;

    /**
     * 当前偷看的 token
     */
    Token peekedToken = null;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.funcs = new ArrayList<>();
        this.decls = new ArrayList<>();
    }

    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     *
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     *
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    public Program analyseProgram() throws CompileError {
        while (true) {
            if (this.check(TokenType.FnKw)) {
                var parseRes = this.parseFuncDecl();
                funcs.add(parseRes);
            } else if (this.check(TokenType.LetKw)) {
                var parseRes = this.parseDecl();
                decls.add(parseRes);
            } else if (this.check(TokenType.ConstKw)) {
                var parseRes = this.parseConstDecl();
                this.decls.add(parseRes);
            } else {
                break;
            }
        }
        expect(TokenType.EOF);
        return new Program(funcs, decls);
    }

    private FuncStmt parseFuncDecl() throws CompileError {
        expect(TokenType.FnKw);
        var fn_name = this.parseIdent();
        expect(TokenType.LParen);
        if (!check(TokenType.RParen)) {
            var params = processParams();
            expect(TokenType.Arrow);
            var retType = parseType();
            var funcBody = parseBlock();
            return new FuncStmt(fn_name, params, retType, funcBody);
        } else {
            expect(TokenType.RParen);
            expect(TokenType.Arrow);
            var retType = parseType();
            var funcBody = parseBlock();
            return new FuncStmt(fn_name, null, retType, funcBody);
        }
    }

    private DeclStmt parseDecl() throws CompileError {
        expect(TokenType.LetKw);
        var ident = parseIdent();
        expect(TokenType.Colon);
        var identType = parseType();
        if (check(TokenType.Assign)) {
            expect(TokenType.Assign);
            var value = parseExpr();
            expect(TokenType.Semicolon);
            return new DeclStmt(false, ident, value, identType);
        } else {
            expect(TokenType.Semicolon);
            return new DeclStmt(false, ident, null, identType);
        }
    }

    private DeclStmt parseConstDecl() throws CompileError {
        expect(TokenType.ConstKw);
        var ident = parseIdent();
        expect(TokenType.Colon);
        var identType = parseType();
        expect(TokenType.Assign);
        var value = parseExpr();
        expect(TokenType.Semicolon);
        return new DeclStmt(true, ident, value, identType);
    }

    private Ident parseIdent() throws CompileError {
        return new Ident(this.expect(TokenType.Ident).getValueString());
    }

    private ArrayList<FuncParam> processParams() throws CompileError {
        ArrayList<FuncParam> params = new ArrayList<>();
        while (true) {
            var isConst = check(TokenType.ConstKw);
            if (isConst)
                next();
            var paramName = parseIdent();
            expect(TokenType.Colon);
            var paramType = parseType();
            params.add(new FuncParam(isConst, paramName, paramType));
            if (!check(TokenType.Comma))
                break;
            expect(TokenType.Comma);
        }
        expect(TokenType.RParen);
        return params;
    }

    private TyDef parseType() throws CompileError {
        return new TyDef(expect(TokenType.Ident).getValueString());
    }

    private BlockStmt parseBlock() throws CompileError {
        expect(TokenType.LBrace);
        var blockStmts = parseStmt();
        expect(TokenType.RBrace);
        return new BlockStmt(blockStmts);
    }

    private IfStmt parseIfStmt() throws CompileError {
        expect(TokenType.IfKw);
        var ifCond = parseExpr();
        var ifBlock = parseBlock();

        if (check(TokenType.ElseKw)) {
            expect(TokenType.ElseKw);
            if (check(TokenType.IfKw)) {
                var elseIf = parseIfStmt();
                return new IfStmt(ifCond, ifBlock, new IfElseIf(elseIf));
            } else {
                var elseBlock = parseBlock();
                return new IfStmt(ifCond, ifBlock, new IfElseBlock(elseBlock));
            }
        } else {
            return new IfStmt(ifCond, ifBlock, null);
        }
    }

    private WhileStmt parseWhileStmt() throws CompileError {
        expect(TokenType.WhileKw);
        var whileCond = parseExpr();
        var whileBlock = parseBlock();
        return new WhileStmt(whileCond, whileBlock);
    }

    private EmptyStmt parseEmptyStmt() throws CompileError {
        expect(TokenType.Semicolon);
        return new EmptyStmt();
    }

    private ReturnStmt parseReturnStmt() throws CompileError {
        expect(TokenType.ReturnKw);
        if (!check(TokenType.Semicolon)) {
            var retExpr = parseExpr();
            expect(TokenType.Semicolon);
            return new ReturnStmt(retExpr);
        }
        expect(TokenType.Semicolon);
        return new ReturnStmt(null);
    }

    private ContinueStmt parseContinueStmt() throws CompileError {
        expect(TokenType.ContinueKw);
        expect(TokenType.Semicolon);
        return new ContinueStmt();
    }

    private BreakStmt parseBreakStmt() throws CompileError {
        expect(TokenType.BreakKw);
        expect(TokenType.Semicolon);
        return new BreakStmt();
    }

    private Expr parseExprStmt() throws CompileError {
        var expr = parseExpr();
        expect(TokenType.Semicolon);
        return expr;
    }

    private Expr parseExpr() throws CompileError {
        var leftExpr = parseUnaryExpr();
        return parseExprOpg(leftExpr, 0);
    }

    private Expr parseUnaryExpr() throws CompileError {
        int preOpsCount = 0;
        while (check(TokenType.Minus)) {
            next();
            preOpsCount++;
        }
        var item = parseItem();
        if (preOpsCount % 2 == 0)
            item = new UnaryExpr(TokenType.Plus, item);
        else
            item = new UnaryExpr(TokenType.Minus, item);
        while (check(TokenType.AsKw)) {
            next();
            var exprType = parseType();
            item = new AsExpr(exprType, item);
        }
        return item;
    }

    private Expr parseItem() throws CompileError {
        if (check(TokenType.Ident)) {
            var ident = new Ident(next().getValueString());
            if (check(TokenType.LParen)) {
                return parseFuncCall(ident);
            } else {
                return ident;
            }
        } else if (check(TokenType.UIntLiteral) || check(TokenType.CharLiteral)) {
            var num = next();
            return new LiteralExpr(LiteralType.Integer, num);
        } else if (check(TokenType.DoubleLiteral)) {
            var num = next();
            return new LiteralExpr(LiteralType.Float, num);
        } else if (check(TokenType.StringLiteral)) {
            var string = next();
            return new LiteralExpr(LiteralType.String, string);
        } else if (check(TokenType.LParen)) {
            expect(TokenType.LParen);
            var expr = parseExpr();
            expect(TokenType.RParen);
            return expr;
        } else {
            throw new CompileError() {
                @Override
                public ErrorCode getErr() {
                    return ErrorCode.ItemError;
                }

                @Override
                public Pos getPos() {
                    return new Pos(-1, -1);
                }
            };
        }
    }

    private CallExpr parseFuncCall(Ident funName) throws CompileError {
        expect(TokenType.LParen);
        if (!check(TokenType.RParen)) {
            var params = processCallParams();
            return new CallExpr(funName, params);
        } else {
            expect(TokenType.RParen);
            return new CallExpr(funName, null);
        }
    }

    private ArrayList<Expr> processCallParams() throws CompileError {
        ArrayList<Expr> params = new ArrayList<Expr>();
        while (true) {
            var param = parseExpr();
            if (check(TokenType.Comma)) {
                next();
                params.add(param);
                continue;
            } else {
                params.add(param);
                break;
            }
        }
        expect(TokenType.RParen);
        return params;
    }

    private Expr parseExprOpg(Expr lhs, int precedence) throws CompileError {
        while (isBinaryOp(peek()) && getPrecedence(peek()) >= precedence) {
            var op = next();
            var rhs = parseUnaryExpr();
            while (isBinaryOp(peek()) && ((getPrecedence(peek()) > getPrecedence(op) && isLeftAssoc(peek()))
                    || (getPrecedence(peek()) == getPrecedence(op) && !isLeftAssoc(peek())))) {
                rhs = parseExprOpg(rhs, getPrecedence(peek()));
            }
            lhs = combineExpr(lhs, op, rhs);
        }
        return lhs;
    }

    private Expr combineExpr(Expr lhs, Token op, Expr rhs) throws CompileError {
        if (op.getTokenType() == TokenType.Assign) {
            return new AssignExpr(((UnaryExpr) lhs).getExpr(), rhs, false);
        } else if (isBinaryOp(op)) {
            return new BinaryExpr(lhs, rhs, op.getTokenType());
        } else {
            throw new CompileError() {
                @Override
                public ErrorCode getErr() {
                    return ErrorCode.NotBinaryOp;
                }

                @Override
                public Pos getPos() {
                    return op.getStartPos();
                }
            };
        }
    }

    private ArrayList<Stmt> parseStmt() throws CompileError {
        ArrayList<Stmt> stmts = new ArrayList<>();
        while (!check(TokenType.RBrace)) {
            if ((check(TokenType.ConstKw))) {
                stmts.add(parseConstDecl());
            } else if (check(TokenType.LetKw)) {
                stmts.add(parseDecl());
            } else if (check(TokenType.LBrace)) {
                stmts.add(parseBlock());
            } else if (check(TokenType.IfKw)) {
                stmts.add(parseIfStmt());
            } else if (check(TokenType.WhileKw)) {
                stmts.add(parseWhileStmt());
            } else if (check(TokenType.BreakKw)) {
                stmts.add(parseBreakStmt());
            } else if (check(TokenType.ContinueKw)) {
                stmts.add(parseContinueStmt());
            } else if (check(TokenType.ReturnKw)) {
                stmts.add(parseReturnStmt());
            } else if (check(TokenType.Semicolon)) {
                stmts.add(parseEmptyStmt());
            } else {
                stmts.add(parseExprStmt());
            }
        }
        return stmts;
    }

    private boolean isBinaryOp(Token op) {
        switch (op.getTokenType()) {
            case Plus:
                return true;
            case Minus:
                return true;
            case Mul:
                return true;
            case Div:
                return true;
            case Assign:
                return true;
            case Eq:
                return true;
            case Neq:
                return true;
            case Lt:
                return true;
            case Gt:
                return true;
            case Le:
                return true;
            case Ge:
                return true;
            default:
                return false;
        }
    }

    private int getPrecedence(Token op) throws CompileError {
        switch (op.getTokenType()) {
            case Plus:
                return 10;
            case Minus:
                return 10;
            case Mul:
                return 20;
            case Div:
                return 20;
            case Assign:
                return 1;
            case Eq:
                return 2;
            case Neq:
                return 2;
            case Lt:
                return 2;
            case Gt:
                return 2;
            case Le:
                return 2;
            case Ge:
                return 2;
            default:
                throw new CompileError() {
                    @Override
                    public ErrorCode getErr() {
                        return ErrorCode.NotBinaryOp;
                    }

                    @Override
                    public Pos getPos() {
                        return op.getStartPos();
                    }
                };
        }
    }

    private boolean isLeftAssoc(Token op) throws CompileError {
        switch (op.getTokenType()) {
            case Plus:
                return true;
            case Minus:
                return true;
            case Mul:
                return true;
            case Div:
                return true;
            case Eq:
                return true;
            case Neq:
                return true;
            case Lt:
                return true;
            case Gt:
                return true;
            case Le:
                return true;
            case Ge:
                return true;
            case Assign:
                return false;
            default:
                throw new CompileError() {
                    @Override
                    public ErrorCode getErr() {
                        return ErrorCode.NotBinaryOp;
                    }

                    @Override
                    public Pos getPos() {
                        return op.getStartPos();
                    }
                };
        }
    }

}
