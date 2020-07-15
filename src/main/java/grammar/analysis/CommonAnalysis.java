package grammar.analysis;
import grammar.Grammar;
import grammar.Symbol;

import java.util.*;

public class CommonAnalysis {

    public static Set<Symbol> first(List<Symbol> symbols, Map<Symbol, Set<Symbol>> firstTable) {
        Set<Symbol> result = new HashSet<>();
        for (Symbol s : symbols) {
            if (s.isTerminator()) {
                result.add(s);
                return result;
            }
            result.addAll(firstTable.get(s));
            if (firstTable.get(s).contains(Symbol.EMPTY)) {
                result.remove(Symbol.EMPTY);
            } else {
                return result;
            }
        }
        result.add(Symbol.END);
        return result;
    }

    public static Map<Symbol, Set<Symbol>> firstTable(Grammar g) {
        Map<Symbol, Set<Symbol>> firstTable = initTable(g);

        for (var r : g.getRules()) {
            Symbol pivot = r.getLhs();
            firstTable.get(pivot).add(r.first());
        }

        replaceNonTerminators(firstTable);
        handleEmpty(g, firstTable);
        return firstTable;
    }

    public static Map<Symbol, Set<Symbol>> followTable(Grammar g) {
        return followTable(g, firstTable(g));
    }

    public static Map<Symbol, Set<Symbol>> followTable(Grammar g, Map<Symbol, Set<Symbol>> firstTable) {
        Map<Symbol, Set<Symbol>> followTable = initTable(g);

        followTable.get(g.getStart()).add(Symbol.END);

        for (var n : g.getNonTerminators()) {
            final Set<Symbol> symbolSet = followTable.get(n);
            for (var r : g.getRules()) {
                Symbol toFind = n;
                while (toFind != null) {
                    Symbol next = r.follow(toFind);
                    if (next == null) { break; }
                    if (next == Symbol.END) {
                        if (r.getLhs() != n) { symbolSet.add(r.getLhs()); }
                        break;
                    }
                    if (next.isNonTerminator()) {
                        Set<Symbol> nextFirst = firstTable.get(next);
                        symbolSet.addAll(nextFirst);
                        if (nextFirst.contains(Symbol.EMPTY)) {
                            symbolSet.remove(Symbol.EMPTY);
                            toFind = next;
                        }
                    } else {
                        symbolSet.add(next);
                        break;
                    }
                }
            }
        }
        replaceNonTerminators(followTable);
        return followTable;
    }

    public static Stack<Symbol> convertInputToStack(String string, Grammar g) {
        Stack<Symbol> r = new Stack<>();
        r.push(Symbol.END);
        for (int i = string.length() - 1; i >= 0; i--) {
            final Symbol symbol = g.getSymbol(string.charAt(i));
            if (symbol == null) { throw new IllegalArgumentException("输入串拥有非法字符。"); }
            r.push(symbol);
        }
        return r;
    }

    private static void handleEmpty(Grammar g, Map<Symbol, Set<Symbol>> firstTable) {
        boolean keepProcessing = true;
        while (keepProcessing) {
            keepProcessing = false;
            for (var r : g.getRules()) {
                int i = 0;
                while (i < r.getRhs().size() && r.getRhs().get(i).isNonTerminator() &&
                    firstTable.get(r.getRhs().get(i)).contains(Symbol.EMPTY)) {
                    final Symbol next = r.getRhs().get(i + 1);
                    if (next.isTerminator()) {
                        firstTable.get(r.getLhs()).add(next);
                    } else {
                        firstTable.get(r.getLhs()).addAll(firstTable.get(next));
                    }
                    i++;
                }
                if (i == r.getRhs().size()) {
                    firstTable.get(r.getLhs()).add(Symbol.EMPTY);
                    keepProcessing = true;
                } else {
                    final Symbol next = r.getRhs().get(i);
                    if (next.isTerminator()) {
                        firstTable.get(r.getLhs()).add(next);
                    } else {
                        firstTable.get(r.getLhs()).addAll(firstTable.get(next));
                    }
                }
            }
        }
    }

    private static void replaceNonTerminators(Map<Symbol, Set<Symbol>> table) {
        Map<Symbol, Boolean> hasNonTerminators = new HashMap<>();
        for (var entry : table.entrySet()) {
            hasNonTerminators.put(entry.getKey(), true);
        }
        boolean keepProcessing = true;
        while (keepProcessing) {
            keepProcessing = false;
            for (var entry : table.entrySet()) {
                final Symbol pivot = entry.getKey();
                if (!hasNonTerminators.get(pivot)) { continue; }
                boolean allIsT = true;
                for (var symbol : entry.getValue()) {
                    if (symbol.isNonTerminator()) {
                        Set<Symbol> pivotTable = table.get(pivot);
                        pivotTable = new HashSet<>(pivotTable);
                        pivotTable.addAll(table.get(symbol));
                        pivotTable.remove(pivot);
                        pivotTable.remove(symbol);
                        table.put(pivot, pivotTable);
                        allIsT = false;
                    }
                }
                hasNonTerminators.put(pivot, !allIsT);
            }

            for (var entry : table.entrySet()) {
                keepProcessing |= hasNonTerminators.get(entry.getKey());
            }
        }
    }

    private static Map<Symbol, Set<Symbol>> initTable(Grammar g) {
        Map<Symbol, Set<Symbol>> firstTable = new HashMap<>();
        for (var n : g.getNonTerminators()) {
            firstTable.put(n, new HashSet<>());
        }
        return firstTable;
    }

}
