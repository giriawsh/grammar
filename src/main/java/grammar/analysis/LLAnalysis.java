package grammar.analysis;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.ui.Printer;

import java.util.*;

public class LLAnalysis {

    public static Map<Grammar, Table<Symbol, Symbol, Rule>> llTableMap = new HashMap<>();

    public static Table<Symbol, Symbol, Rule> analysis(Grammar g) {
        Table<Symbol, Symbol, Rule> llTable = HashBasedTable.create();
        Map<Symbol, Set<Symbol>> firstTable = CommonAnalysis.firstTable(g);
        Map<Symbol, Set<Symbol>> followTable = CommonAnalysis.followTable(g, firstTable);
        for (Symbol n : g.getNonTerminators()) {
            for (Symbol t : g.getTerminators()) {
                if (t == Symbol.EMPTY) { continue; }
                llTable.put(n, t, Rule.SYNC);
            }
            llTable.put(n, Symbol.END, Rule.SYNC);
        }
        for (Rule r : g.getRules()) {
            final Symbol pivot = r.getLhs();
            final Set<Symbol> firstSet = CommonAnalysis.first(r.getRhs(), firstTable);
            for (Symbol symbol : firstSet) {
                if (symbol == Symbol.EMPTY) { continue; }
                llTable.put(pivot, symbol, r);
            }
            if (firstSet.contains(Symbol.EMPTY)) {
                for (Symbol symbol : followTable.get(pivot)) {
                    if (symbol == Symbol.EMPTY) { continue; }
                    llTable.put(pivot, symbol, r);
                }
            }
        }
        llTableMap.put(g, llTable);
        return llTable;
    }

    public static void analysis(String inputString, Grammar g) {
        Table<Symbol, Symbol, Rule> llTable;
        if (llTableMap.containsKey(g)) {
            llTable = llTableMap.get(g);
        } else {
            llTable = analysis(g);
        }
        Stack<Symbol> input = CommonAnalysis.convertInputToStack(inputString, g);
        Stack<Symbol> stack = new Stack<>();
        stack.push(Symbol.END);
        stack.push(g.getStart());
        Printer.printTitle();
        while (true) {
            Printer.printStack(input);
            Printer.printStack(stack);
            Rule rule;
            final Symbol stackTop = stack.peek();
            final Symbol c = input.peek();
            if (stackTop == Symbol.END && c == Symbol.END) {
                System.out.println("ACC");
                return;
            }
            if (stackTop.isTerminator()) {
                if (stackTop == c) {
                    stack.pop();
                    input.pop();
                    System.out.println();
                } else {
                    error("栈与输入不匹配");
                    return;
                }
            } else {
                rule = llTable.get(stackTop, c);
                if (rule == Rule.SYNC) {
                    error("没有对应规则");
                    return;
                }
                stack.pop();
                System.out.println(rule);
                if (rule.first() != Symbol.EMPTY) {
                    ListIterator<Symbol> listIterator = rule.getRhs().listIterator(rule.getRhs().size());
                    while (listIterator.hasPrevious()) {
                        stack.push(listIterator.previous());
                    }
                }
            }
        }
    }

    private static void error(String s) {
        System.out.println("Error: " + s);

    }


}
