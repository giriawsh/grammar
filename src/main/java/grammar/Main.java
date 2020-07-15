package grammar;

import grammar.analysis.LLAnalysis;
import grammar.analysis.LRAnalysis;
import grammar.analysis.LRResult;
import grammar.ui.Printer;

import java.util.List;
import java.util.Set;

public class Main {
    private static Symbol S = new NonTerminator('S');

    private static Symbol E = new NonTerminator('E');

    private static Symbol A = new NonTerminator('A');

    private static Symbol T = new NonTerminator('T');

    private static Symbol F = new NonTerminator('F');

    private static Symbol B = new NonTerminator('B');

    private static Symbol e = Symbol.EMPTY;

    private static Symbol divide = new Terminator('/');

    private static Symbol minus = new Terminator('-');

    private static Symbol plus = new Terminator('+');

    private static Symbol mul = new Terminator('*');

    private static Symbol lb = new Terminator('(');

    private static Symbol rb = new Terminator(')');

    private static Symbol n = new Terminator('n');

    private static Symbol $ = Symbol.END;

    private static Rule r1 = new Rule(E, List.of(T, A));

    private static Rule r2 = new Rule(A, List.of(minus, T, A));

    private static Rule r3 = new Rule(A, List.of(plus, T, A));

    private static Rule r4 = new Rule(A, List.of(e));

    private static Rule r5 = new Rule(T, List.of(F, B));

    private static Rule r6 = new Rule(B, List.of(mul, F, B));

    private static Rule r7 = new Rule(B, List.of(divide, F, B));

    private static Rule r8 = new Rule(B, List.of(e));

    private static Rule r9 = new Rule(F, List.of(lb, E, rb));

    private static Rule r10 = new Rule(F, List.of(n));

    private static Rule r11 = new Rule(E, List.of(E, plus, T));
    private static Rule r12 = new Rule(E, List.of(E, minus, T));
    private static Rule r13 = new Rule(E, List.of(T));
    private static Rule r14 = new Rule(T, List.of(T, mul, F));
    private static Rule r15 = new Rule(T, List.of(T, divide, F));
    private static Rule r16 = new Rule(T, List.of(F));
    private static Rule r17 = new Rule(S, List.of(E));


    private static Grammar g1 =
            new Grammar(Set.of(E, T, B, A, F, mul, divide, plus, minus, e, n, lb, rb), E, Set.of(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10));

    private static Grammar g2 =
            new Grammar(Set.of(S, E, T, F, mul, divide, plus, minus, n, lb, rb), S, Set.of(r9, r10, r11, r12, r13, r14, r15, r16, r17));


    public static void main(String[] args) {
        Printer.printLLTable(LLAnalysis.analysis(g1));
        LLAnalysis.analysis("n+n)", g1);
        LRResult analysis = LRAnalysis.analysis(g2);
        System.out.println(analysis);
//        System.out.println(analysis.getInitState());
        LRAnalysis.analysis("((n*n+n/n)-n*n*n+(n+n)/(n-n))", g2);
    }
}