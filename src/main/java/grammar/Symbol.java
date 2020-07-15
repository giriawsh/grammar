package grammar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Symbol {

    public static final Symbol END = new Symbol('$', true);

    public static final Symbol EMPTY = new Symbol('ε', true);

    private char c;

    private boolean isTerminator;

    @Override
    public String toString() {
        return String.valueOf(c);
    }

    public boolean isNonTerminator() {
        return !isTerminator;
    }

}
