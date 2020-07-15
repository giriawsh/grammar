package grammar;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;

import java.util.Objects;

@Getter
public class LR0Item {

    private static final Table<Rule, Integer, LR0Item> pool = HashBasedTable.create();

    private final Rule rule;

    private final int pointPosition;

    private final boolean isReducible;

    private final Symbol pointNext;

    public LR0Item(Rule rule, int pointPosition) {
        this.rule = rule;
        this.pointPosition = pointPosition;
        isReducible = pointPosition == rule.getRhs().size() || rule.first() == Symbol.EMPTY;
        if (isReducible()) {
            pointNext = null;
        } else {
            pointNext = rule.getRhs().get(pointPosition);
        }
    }

    public static LR0Item getInstance(Rule r, int pointPosition) {
        if (!pool.contains(r, pointPosition)) {
            pool.put(r, pointPosition, new LR0Item(r, pointPosition));
        }
        return pool.get(r, pointPosition);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(rule.getLhs() + "->");
        for (int i = 0; i < rule.getRhs().size(); i++) {
            if (i == pointPosition) {
                stringBuilder.append('·');
            }
            stringBuilder.append(rule.getRhs().get(i));
        }
        return stringBuilder.toString();
    }

    public Symbol pointNext() {
        return pointNext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LR0Item lr0Item = (LR0Item) o;
        return pointPosition == lr0Item.pointPosition &&
            rule.equals(lr0Item.rule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, pointPosition);
    }

}
