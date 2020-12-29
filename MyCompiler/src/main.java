import Stmts.Program;
import analyser.Analyser;
import tokenizer.StringIter;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class main {

    StringIter it;

    public static void main(String[] args) {
        FileInputStream input;
        try {
            input = new FileInputStream("test_Analyser.txt");
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find this file");
            return;
        }
        Scanner sc;
        sc = new Scanner(input);
        StringIter iter = new StringIter(sc);
        Tokenizer tokenizer = new Tokenizer(iter);
        Analyser analyser = new Analyser(tokenizer);
        try {
            Program program = analyser.analyseProgram();
            System.out.println(program);
        } catch (Exception e) {
            System.out.println(e);
        }
//        Token temp;
//        while (true) {
//            try {
//                temp = tokenizer.nextToken();
//                if (temp.getTokenType() == TokenType.EOF)
//                    return;
//                System.out.println(temp.getValueString());
////                System.out.println(tokenizer.nextToken());
//            } catch (Exception e) {
//                System.out.println("Tokenizer Error");
////                return;
//            }
//        }
    }

}
