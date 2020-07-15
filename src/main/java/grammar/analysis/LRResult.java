package grammar.analysis;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import grammar.Grammar;
import grammar.LR0Item;
import grammar.LR0ItemSet;
import grammar.Symbol;
import lombok.Getter;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static grammar.ui.Printer.getFormat;

public class LRResult {

    private final Grammar g;

    private final Table<LR0ItemSet, Symbol, LR0ItemSet> transferTable = HashBasedTable.create();

    private final Table<LR0ItemSet, Symbol, LRAction> analysisTable = HashBasedTable.create();

    private final Map<LR0ItemSet, Integer> idTable = new HashMap<>();

    private final Map<Integer, LR0ItemSet> idTableReverse = new HashMap<>();

    private final Map<Symbol, Set<Symbol>> followTable;

    @Getter
    private final Set<LR0ItemSet> allStates = new HashSet<>();

    @Getter
    private LR0ItemSet initState;

    private int id = 0;

    public LRResult(Grammar g) {
        this.g = g;
        followTable = CommonAnalysis.followTable(g);
    }

    public void addState(LR0ItemSet state) {
        idTable.put(state, id);
        idTableReverse.put(id++, state);
        allStates.add(state);
        calculateReduceAction(state);
    }

    public void addTransfer(LR0ItemSet start, Symbol s, LR0ItemSet end) {
        transferTable.put(start, s, end);
        if (s.isNonTerminator()) {
            analysisTable.put(start, s, LRAction.goTo(end));
        } else {
            analysisTable.put(start, s, LRAction.shift(end));
        }
    }

    public LR0ItemSet nextState(LR0ItemSet state, Symbol s) {
        return transferTable.get(state, s);
    }

    public LRAction getAction(LR0ItemSet state, Symbol s) {
        return analysisTable.get(state, s);
    }

    public LR0ItemSet getState(int id) {
        return idTableReverse.get(id);
    }

    public int getId(LR0ItemSet set) {
        return idTable.get(set);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getFormat(""));
        for (Symbol s : analysisTable.columnKeySet()) {
            builder.append(getFormat(s.toString()));
        }
        for (LR0ItemSet row : analysisTable.rowKeySet()) {
            builder.append('\n');
            builder.append(getFormat(idTable.get(row).toString()));
            for (Symbol col : analysisTable.columnKeySet()) {
                final LRAction lrAction = analysisTable.get(row, col);
                if (lrAction != null) {
                    String representation = null;
                    switch (lrAction.getType()) {
                        case shift:
                            representation = "shift " + idTable.get(lrAction.getState()).toString();
                            break;
                        case goTo:
                            representation = idTable.get(lrAction.getState()).toString();
                            break;
                        case reduce:
                        case accept:
                            representation = lrAction.toString();
                    }
                    builder.append(getFormat(representation));
                } else {
                    builder.append(getFormat(""));
                }
            }
        }
        return builder.toString();
    }

    private void calculateReduceAction(LR0ItemSet state) {
        for (LR0Item reducible : state.findReducible()) {
            for (Symbol s : followTable.get(reducible.getRule().getLhs())) {
                analysisTable.put(state, s, LRAction.reduce(reducible.getRule()));
            }
            if (reducible.getRule().getLhs() == g.getStart()) {
                analysisTable.put(state, Symbol.END, LRAction.accept());
            }
        }
    }

    public void setInitState(LR0ItemSet initState) {
        this.initState = initState;
        addState(initState);
    }

}


