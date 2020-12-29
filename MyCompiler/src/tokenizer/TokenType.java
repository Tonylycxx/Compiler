package tokenizer;

public enum TokenType {
    /** EOF */
    EOF,
    /** fn */
    FnKw,
    /** let */
    LetKw,
    /** const */
    ConstKw,
    /** as */
    AsKw,
    /** while */
    WhileKw,
    /** if */
    IfKw,
    /** else */
    ElseKw,
    /** return */
    ReturnKw,
    /** break */
    BreakKw,
    /** continue */
    ContinueKw,


    /** uint */
    UIntLiteral,
    /** float */
    DoubleLiteral,
    /** char */
    CharLiteral,
    /** string */
    StringLiteral,
    /** ident */
    Ident,


    /** + */
    Plus,
    /** - */
    Minus,
    /** * */
    Mul,
    /** / */
    Div,
    /** = */
    Assign,
    /** = */
    Eq,
    /** != */
    Neq,
    /** < */
    Lt,
    /** > */
    Gt,
    /** <= */
    Le,
    /** >= */
    Ge,
    /** ( */
    LParen,
    /** ) */
    RParen,
    /** { */
    LBrace,
    /** } */
    RBrace,
    /** -> */
    Arrow,
    /** , */
    Comma,
    /** : */
    Colon,
    /** ; */
    Semicolon,
    /** // */
    Comment;

    @Override
    public String toString() {
        switch (this) {
            case EOF:
                return "eof";
            case FnKw:
                return "fn";
            case LetKw:
                return "let";
            case ConstKw:
                return "const";
            case AsKw:
                return "as";
            case WhileKw:
                return "while";
            case IfKw:
                return "if";
            case ElseKw:
                return "else";
            case ReturnKw:
                return "return";
            case BreakKw:
                return "break";
            case ContinueKw:
                return "continue";
            case UIntLiteral:
                return "uint";
            case DoubleLiteral:
                return "double";
            case CharLiteral:
                return "char";
            case StringLiteral:
                return "string";
            case Ident:
                return "ident";
            case Plus:
                return "+";
            case Minus:
                return "-";
            case Mul:
                return "*";
            case Div:
                return "/";
            case Assign:
                return "=";
            case Eq:
                return "==";
            case Neq:
                return "!=";
            case Lt:
                return "<";
            case Gt:
                return ">";
            case Le:
                return "<=";
            case Ge:
                return ">=";
            case LParen:
                return "(";
            case RParen:
                return ")";
            case LBrace:
                return "{";
            case RBrace:
                return "}";
            case Arrow:
                return "->";
            case Comma:
                return ",";
            case Colon:
                return ":";
            case Semicolon:
                return ";";
            case Comment:
                return "//";
            default:
                return "InvalidToken";
        }
    }
}
