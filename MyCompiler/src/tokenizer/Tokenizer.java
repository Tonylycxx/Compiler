package tokenizer;

import error.TokenizeError;
import error.ErrorCode;
import util.Pos;

import java.util.ArrayList;

public class Tokenizer {

    private StringIter it;

    private String[] keyWordsArray = {"fn", "let", "const", "as", "while", "if", "else", "return", "break", "continue"};
    private TokenType[] keyWordsType = {TokenType.FnKw, TokenType.LetKw, TokenType.ConstKw, TokenType.AsKw, TokenType.WhileKw, TokenType.IfKw, TokenType.ElseKw, TokenType.ReturnKw, TokenType.BreakKw, TokenType.ContinueKw};
    private ArrayList<String> keyWords;

    private TokenRegular tokenRegular = new TokenRegular();

    public Tokenizer(StringIter it) {
        this.it = it;
        this.keyWords = new ArrayList<String>();
        for (int i = 0; i < this.keyWordsArray.length; i++)
            this.keyWords.add(this.keyWordsArray[i]);
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();
//        while(!it.isEOF())
//            System.out.print(it.nextChar());

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "eof", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return lexUIntOrFloat();
        } else if (Character.isAlphabetic(peek) || peek == '_') {
            return lexIdentOrKeyword();
        } else if (peek == '"') {
            return lexString();
        } else if (peek == '\'') {
            return lexChar();
        } else {
            Token res = lexOperatorOrUnknown();
            if(res.getTokenType() == TokenType.Comment) {
                 return nextToken();
            } else {
                return res;
            }
//            return new Token(TokenType.Arrow, "->", it.currentPos(), it.currentPos());
        }
    }

    private Token lexUIntOrFloat() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
        Pos startPos = it.currentPos();
        String token;
        StringBuilder tokenBuiler = new StringBuilder("");
        while (Character.isDigit(it.peekChar())) {
            tokenBuiler.append(it.nextChar());
        }
        if (it.peekChar() == '.') {
            tokenBuiler.append(it.nextChar());
            while (Character.isDigit(it.peekChar())) {
                tokenBuiler.append(it.nextChar());
            }
            if (it.peekChar() == 'E' || it.peekChar() == 'e') {
                tokenBuiler.append(it.nextChar());
                if (it.peekChar() == '+' || it.peekChar() == '-') {
                    tokenBuiler.append(it.nextChar());
                    while (Character.isDigit(it.peekChar()))
                        tokenBuiler.append(it.nextChar());
                    token = tokenBuiler.toString();
                    if(tokenRegular.isDouble(token))
                        return new Token(TokenType.DoubleLiteral, Double.parseDouble(token), startPos, it.currentPos());
                    else
                        throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                } else if (Character.isDigit(it.peekChar())) {
                    while (Character.isDigit(it.peekChar()))
                        tokenBuiler.append(it.nextChar());
                    token = tokenBuiler.toString();
                    if(tokenRegular.isDouble(token))
                        return new Token(TokenType.DoubleLiteral, Double.parseDouble(token), startPos, it.currentPos());
                    else
                        throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                } else {
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }
            } else {
                token = tokenBuiler.toString();
                if(tokenRegular.isDouble(token))
                    return new Token(TokenType.DoubleLiteral, Double.parseDouble(token), startPos, it.currentPos());
                else
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            }
        } else {
            token = tokenBuiler.toString();
            return new Token(TokenType.UIntLiteral, Long.parseLong(token), startPos, it.currentPos());
        }

    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        Pos startPos = it.currentPos();
        StringBuilder tokenBuiler = new StringBuilder("");
        tokenBuiler.append(it.nextChar());
        while (Character.isAlphabetic(it.peekChar()) || it.peekChar() == '_' || Character.isDigit(it.peekChar())) {
            tokenBuiler.append(it.nextChar());
        }
        String token = tokenBuiler.toString();
        if (keyWords.contains(token)) {
            return new Token(keyWordsType[keyWords.indexOf(token)], token, startPos, it.currentPos());
        } else if (tokenRegular.isIdent(token)) {
            return new Token(TokenType.Ident, token, startPos, it.currentPos());
        } else {
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private Token lexString() throws TokenizeError {
        Pos startPos = it.currentPos();
        StringBuilder tokenBuiler = new StringBuilder("");
        char lastChar = it.nextChar();
        tokenBuiler.append(lastChar);
        while (it.peekChar() != '"' || lastChar == '\\') {
            lastChar = it.nextChar();
            tokenBuiler.append(lastChar);
        }
        if (!Character.isWhitespace(it.peekChar()))
            tokenBuiler.append(it.nextChar());
        String token = tokenBuiler.toString();
        if (tokenRegular.isString(token)) {
            return new Token(TokenType.StringLiteral, this.handleEscapeChar(token.substring(1, token.length() - 1)), startPos, it.currentPos());
        } else {
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private Token lexChar() throws TokenizeError {
        Pos startPos = it.currentPos();
        StringBuilder tokenBuiler = new StringBuilder("");
        String token;
        char lastChar = it.nextChar();
        tokenBuiler.append(lastChar);
        if (it.peekChar() == '\\') {
            tokenBuiler.append(it.nextChar());
            tokenBuiler.append(it.nextChar());
            if (it.peekChar() == '\'') {
                tokenBuiler.append(it.nextChar());
                token = tokenBuiler.toString();
                if (tokenRegular.isChar(token))
                    return new Token(TokenType.CharLiteral, this.handleEscapeChar(token.substring(1, token.length() - 1)), startPos, it.currentPos());
                else
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            } else {
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            }
        } else {
            tokenBuiler.append(it.nextChar());
            if (it.peekChar() == '\'') {
                tokenBuiler.append(it.nextChar());
                token = tokenBuiler.toString();
                if (tokenRegular.isChar(token))
                    return new Token(TokenType.CharLiteral, token.substring(1, token.length() - 1), startPos, it.currentPos());
                else
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            } else {
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            }
        }
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        Pos startPos = it.currentPos();
        if (it.peekChar() == '+') {
            it.nextChar();
            return new Token(TokenType.Plus, "+", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '-') {
            it.nextChar();
            if (it.peekChar() == '>') {
                it.nextChar();
                return new Token(TokenType.Arrow, "->", startPos, it.currentPos());
            }
            return new Token(TokenType.Minus, "-", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '*') {
            it.nextChar();
            return new Token(TokenType.Mul, "*", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '/') {
            it.nextChar();
            if (it.peekChar() == '/') {
                while (it.peekChar() != '\n')
                    it.nextChar();
                it.nextChar();
                return new Token(TokenType.Comment, "", startPos, it.currentPos());
            }
            return new Token(TokenType.Div, "/", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '=') {
            it.nextChar();
            if (it.peekChar() == '=') {
                it.nextChar();
                return new Token(TokenType.Eq, "==", startPos, it.currentPos());
            }
            return new Token(TokenType.Assign, "=", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '!') {
            it.nextChar();
            if (it.peekChar() == '=') {
                it.nextChar();
                return new Token(TokenType.Neq, "!=", startPos, it.currentPos());
            } else
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        } else if (it.peekChar() == '<') {
            it.nextChar();
            if (it.peekChar() == '=') {
                it.nextChar();
                return new Token(TokenType.Le, "<=", startPos, it.currentPos());
            }
            return new Token(TokenType.Lt, "<", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '>') {
            it.nextChar();
            if (it.peekChar() == '=') {
                it.nextChar();
                return new Token(TokenType.Ge, ">=", startPos, it.currentPos());
            }
            return new Token(TokenType.Gt, ">", it.currentPos(), it.nextPos());
        }  else if (it.peekChar() == '(') {
            it.nextChar();
            return new Token(TokenType.LParen, "(", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == ')') {
            it.nextChar();
            return new Token(TokenType.RParen, ")", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '{') {
            it.nextChar();
            return new Token(TokenType.LBrace, "{", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == '}') {
            it.nextChar();
            return new Token(TokenType.RBrace, "}", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == ',') {
            it.nextChar();
            return new Token(TokenType.Comma, ",", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == ':') {
            it.nextChar();
            return new Token(TokenType.Colon, ":", it.currentPos(), it.nextPos());
        } else if (it.peekChar() == ';') {
            it.nextChar();
            return new Token(TokenType.Semicolon, ";", it.currentPos(), it.nextPos());
        } else {
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }

    private String handleEscapeChar(String strToHandle) {
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < strToHandle.length(); ) {
            if (strToHandle.charAt(i) == '\\') {
                switch (strToHandle.charAt(i + 1)) {
                    case 'n':
                        res.append('\n');
                        i += 2;
                        break;
                    case 't':
                        res.append('\t');
                        i += 2;
                        break;
                    case 'r':
                        res.append('\r');
                        i += 2;
                        break;
                    case '\\':
                        res.append('\\');
                        i += 2;
                        break;
                    case '\'':
                        res.append('\'');
                        i += 2;
                        break;
                    case '"':
                        res.append('"');
                        i += 2;
                        break;
                }
            } else {
                res.append(strToHandle.charAt(i));
                i += 1;
            }
        }
        return res.toString();
    }
}
