package grammar;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class Rule {

    public static Rule SYNC = new Rule(null, null);

    private Symbol lhs;

    private List<Symbol> rhs;

    public Symbol first() {
        return rhs.get(0);
    }

    public Symbol follow(Symbol n) {
        for (int i = 0; i < rhs.size(); i++) {
            if (rhs.get(i) == n) {
                if (i + 1 < rhs.size()) {
                    return rhs.get(i + 1);
                } else {
                    return Symbol.END;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        if (this == SYNC) { return "sync"; }
        return lhs.toString() + "->" + toString(rhs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Rule rule = (Rule) o;
        return lhs.equals(rule.lhs) &&
            rhs.equals(rule.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }

    private String toString(List<Symbol> rhs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol s : rhs) {
            stringBuilder.append(s.toString());
        }
        return stringBuilder.toString();
    }

}
