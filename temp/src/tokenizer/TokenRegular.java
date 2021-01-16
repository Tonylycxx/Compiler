package tokenizer;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenRegular {

//    private Pattern intLiteral = Pattern.compile("^\\d+$");
    private Pattern doubleLiteral = Pattern.compile("^\\d+\\.\\d+([eE][+-]?\\d+)?$");
    private Pattern charLiteral = Pattern.compile("^'([^\\\\']|\\\\[rnt\\\\/\"'])'$");
    private Pattern stringLiteral = Pattern.compile("^\"([^\\\\\"]|\\\\([rnt\\\\/\"']))*\"$");
    private Pattern ident = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*$");

//    public boolean isUint(String str) {
//        Matcher res = intLiteral.matcher(str);
//        return res.find();
//    }

    public boolean isDouble(String str) {
        Matcher res = doubleLiteral.matcher(str);
        return res.find();
    }

    public boolean isChar(String str) {
        Matcher res = charLiteral.matcher(str);
        return res.find();
    }

    public boolean isString(String str) {
        Matcher res = stringLiteral.matcher(str);
        return res.find();
    }

    public boolean isIdent(String str) {
        Matcher res = ident.matcher(str);
        return res.find();
    }

}
