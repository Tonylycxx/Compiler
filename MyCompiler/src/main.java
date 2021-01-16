import C0.C0;
import RawC0.B0;
import Stmts.Program;
import analyser.Analyser;
import generator.Generator;
import tokenizer.StringIter;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class main {

    StringIter it;

    public static void main(String[] args) {
//        String test = "test";
//        byte[] res = test.getBytes();
//        for(byte i : res)
//            System.out.println(i);
//        long a = 10;
//        System.out.println(Long.toHexString(a));
        FileInputStream input;
        try {
            File inputFile = new File(args[1]);
            input = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find this file");
            return;
        }
        Scanner sc;
        sc = new Scanner(input);
        StringIter iter = new StringIter(sc);
        Tokenizer tokenizer = new Tokenizer(iter);
        Analyser analyser = new Analyser(tokenizer);
        Generator generator = new Generator();
        try {
            Program program = analyser.analyseProgram();
//            System.out.println(program);
            B0 b0 = generator.compileProgram(program);
//            System.out.println(b0);
            C0 c0 = new C0(b0, args[3]);
            c0.writeCodeToFile();
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
//            } catch (Exception e) {
//                System.out.println("Tokenizer Error");
////                return;
//            }
//        }
    }

}
