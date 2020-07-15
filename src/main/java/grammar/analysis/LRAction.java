package grammar.analysis;

import grammar.LR0ItemSet;
import grammar.Rule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LRAction {

    private Type type;

    private LR0ItemSet state;

    private Rule rule;

    public static LRAction goTo(LR0ItemSet state) {
        return new LRAction(Type.goTo, state, null);
    }

    public static LRAction shift(LR0ItemSet state) {
        return new LRAction(Type.shift, state, null);
    }

    public static LRAction reduce(Rule rule) {
        return new LRAction(Type.reduce, null, rule);
    }

    public static LRAction accept() {
        return new LRAction(Type.accept, null, null);
    }

    @Override
    public String toString() {
        switch (type) {
            case shift:
                return "s " + state.toString();
            case reduce:
                return "r " + rule.toString();
            case accept:
                return "acc";
        }
        return "error";
    }

    public enum Type {
        shift,
        goTo,
        reduce,
        accept
    }

}
