package grammar.analysis;

import com.google.common.collect.Table;
import grammar.*;
import grammar.ui.Printer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LRAnalysis {

    private static final Map<Grammar, Table<LR0ItemSet, Symbol, LRAction>> lRTableMap = new ConcurrentHashMap<>();

    public static LR0ItemSet closure(Grammar g, LR0Item core) {
        LR0ItemSet closure = new LR0ItemSet();
        closure.add(core);
        boolean changed;
        do {
            changed = false;
            for (LR0Item it : closure) {
                final Symbol symbol = it.pointNext();
                if (symbol == null) {
                    continue;
                }
                if (symbol.isNonTerminator()) {
                    for (Rule r : g.getRules(symbol)) {
                        changed |= closure.add(LR0Item.getInstance(r, 0), !changed);
                    }
                }
            }
        } while (changed);
        return closure;
    }

    public static LR0ItemSet go(Grammar g, LR0ItemSet origin, Symbol s) {
        LR0ItemSet result = new LR0ItemSet();
        LR0ItemSet toGo = origin.findNextIs(s);
        for (LR0Item item : toGo) {
            LR0Item core = LR0Item.getInstance(item.getRule(), item.getPointPosition() + 1);
            result.addAll(closure(g, core));
        }
        return result;
    }

    /**
     * 假定G是拥有起始符单产生式的
     *
     * @param g 文法
     * @return 该文法的LR0项目规范集族
     */
    public static LRResult analysis(Grammar g) {
        Rule startRule = g.getRules(g.getStart()).iterator().next();
        LRResult result = new LRResult(g);
        final LR0ItemSet startState = closure(g, LR0Item.getInstance(startRule, 0));
        result.setInitState(startState);
        Set<LR0ItemSet> toIter = result.getAllStates();
        Set<LR0ItemSet> newStates;
        do {
            newStates = new HashSet<>();
            for (LR0ItemSet start : toIter) {
                for (LR0Item item : start) {
                    final Symbol s = item.pointNext();
                    if (s != null) {
                        final LR0ItemSet end = go(g, start, s);
                        if (!result.getAllStates().contains(end)) {
                            if (newStates.add(end)) {
                                result.addState(end);
                            }
                        }
                        result.addTransfer(start, s, end);
                    }
                }
            }
            toIter = newStates;
        } while (!toIter.isEmpty());
        return result;
    }

    public static void analysis(String inputString, Grammar g) {
        Stack<Symbol> inputStack = CommonAnalysis.convertInputToStack(inputString, g);
        LRResult analysisResult = analysis(g);
        Stack<LR0ItemSet> stateStack = new Stack<>();
        stateStack.push(analysisResult.getInitState());
        while (true) {
            Printer.printStack(stateStack.stream().map(analysisResult::getId).collect(Collectors.toList()));
            Printer.printStack(inputStack);
            final LR0ItemSet state = stateStack.peek();
            final Symbol input = inputStack.peek();
            final LRAction action = analysisResult.getAction(state, input);
            if (action == null) {
                System.out.println("Error");
                return;
            }
            switch (action.getType()) {
                case shift:
                    stateStack.push(action.getState());
                    inputStack.pop();
                    System.out.println("Shift " + analysisResult.getId(action.getState()));
                    break;
                case reduce:
                    Rule r = action.getRule();
                    for (int i = 0; i < r.getRhs().size(); i++) {
                        stateStack.pop();
                    }
                    final LR0ItemSet nextState = analysisResult.getAction(stateStack.peek(), r.getLhs()).getState();
                    stateStack.push(nextState);
                    System.out.println(r + "  goto  " + analysisResult.getId(nextState));
                    break;
                case accept:
                    System.out.println("ACC");
                    return;
            }
        }
    }

}
