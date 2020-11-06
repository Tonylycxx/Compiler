import java.io.*;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

public class OPG {

    int[][] priority_matrix = {{1, 0, 0, 1, 0, 1},
            {1, 1, 0, 1, 0, 1},
            {0, 0, 0, 0, 0, 1},
            {1, 1, -1, 1, -1, 1},
            {1, 1, -1, 1, -1, 1},
            {0, 0, 0, 0, 0, 0}};

    Stack<Character> stack = new Stack<Character>();
    Stack<Character> termLeft = new Stack<Character>();

    public int transChar2Index(char c) {
        switch (c) {
            case '+':
                return 0;
            case '*':
                return 1;
            case '(':
                return 2;
            case ')':
                return 3;
            case 'i':
                return 4;
            case '#':
                return 5;
            default:
                return -1;
        }
    }

    public OPG() {
        stack.push('#');
        termLeft.push('#');
    }

    public void doPlus() throws Exception {
        if (stack.pop() == 'N' && stack.pop() == '+' && stack.pop() == 'N') {
            stack.push('N');
            termLeft.pop();
            System.out.println("R");
        } else {
            throw new Exception("RE");
        }
    }

    public void doMul() throws Exception {
        if (stack.pop() == 'N' && stack.pop() == '*' && stack.pop() == 'N') {
            stack.push('N');
            termLeft.pop();
            System.out.println("R");
        } else {
            throw new Exception("RE");
        }
    }

    public void removeSynthesis() throws Exception {
        if (stack.pop() == ')' && stack.pop() == 'N' && stack.pop() == '(') {
            stack.push('N');
            termLeft.pop();
            termLeft.pop();
            System.out.println("R");
        } else {
            throw new Exception("RE");
        }
    }

    public void doReduce() throws Exception {
        if (termLeft.peek() == 'i') {
            stack.pop();
            stack.push('N');
            termLeft.pop();
            System.out.println("R");
        } else {
            if (termLeft.peek() == '+')
                doPlus();
            else if (termLeft.peek() == '*')
                doMul();
            else if (termLeft.peek() == ')')
                removeSynthesis();
        }
    }

    public void doReduceProcess(char inputChar) throws Exception {
        int row = this.transChar2Index(termLeft.peek());
        int col = this.transChar2Index(inputChar);
        while (this.priority_matrix[row][col] == 1) {
            doReduce();
            row = this.transChar2Index(termLeft.peek());
        }
        if (this.priority_matrix[row][col] == 0) {
            if (inputChar == '#')
                return;
            termLeft.push(inputChar);
            stack.push(inputChar);
        }
        if (this.priority_matrix[row][col] == -1)
            throw new Exception("E");
        return;
    }

    public static void main(String[] args) throws IOException {
        OPG opg = new OPG();
        File inputFile = new File(args[0]);
        FileReader inputReader = new FileReader(inputFile);
        BufferedReader input = new BufferedReader(inputReader);
        String inputString = input.readLine();
        System.out.println(inputString);
        return;
        int loc = 0;
        char inputChar;
        while (true) {
            if (loc == inputString.length())
                inputChar = '#';
            else
                inputChar = inputString.charAt(loc++);
            if (opg.transChar2Index(inputChar) == -1) {
                System.out.println("E");
                return;
            }
            try {
                opg.doReduceProcess(inputChar);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
            if (inputChar == '#')
                break;
            System.out.println("I" + inputChar);
        }
        if (opg.stack.pop() == 'N' && opg.stack.pop() == '#')
            return;
        else
            System.out.println("RE");
    }

}
