package grammar;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grammar {

    private final Map<Symbol, Set<Rule>> symbolRuleMap = new HashMap<>();

    @Getter
    private final Set<Symbol> nonTerminators = new HashSet<>();

    @Getter
    private final Set<Symbol> terminators = new HashSet<>();

    private final Map<Character, Symbol> symbolMap = new HashMap<>();

    @Getter
    private Set<Symbol> symbols;

    @Getter
    private Symbol start;

    @Getter
    private Set<Rule> rules;

    public Grammar(Set<Symbol> symbols, Symbol start, Set<Rule> rules) {
        this.symbols = symbols;
        this.start = start;
        this.rules = rules;
        for (Symbol symbol : this.symbols) {
            if (symbol.isTerminator()) {
                terminators.add(symbol);
            } else {
                nonTerminators.add(symbol);
            }
            symbolMap.put(symbol.getC(), symbol);
        }
        for (Rule r : this.rules) {
            if (!symbolRuleMap.containsKey(r.getLhs())) {
                final HashSet<Rule> set = new HashSet<>();
                set.add(r);
                symbolRuleMap.put(r.getLhs(), set);
            } else {
                symbolRuleMap.get(r.getLhs()).add(r);
            }
        }
    }

    public Symbol getSymbol(char c) {
        return symbolMap.get(c);
    }

    public Set<Rule> getRules(Symbol s) {
        return symbolRuleMap.get(s);
    }

}
