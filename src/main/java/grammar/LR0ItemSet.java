package grammar;

import java.util.*;
import java.util.stream.Collectors;

public class LR0ItemSet implements Iterable<LR0Item> {

    private final Map<Symbol, Set<LR0Item>> nextSymbolTable = new HashMap<>();

    private Set<LR0Item> items;

    public LR0ItemSet() {
        items = new HashSet<>();
    }

    public LR0ItemSet(Set<LR0Item> items) {
        this.items = items;
    }

    public boolean add(LR0Item item) {
        if (!nextSymbolTable.containsKey(item.pointNext())) {
            nextSymbolTable.put(item.pointNext(), new HashSet<>());
        }
        nextSymbolTable.get(item.pointNext()).add(item);
        return items.add(item);
    }

    public boolean add(LR0Item item, boolean copyOnNew) {
        if (items.contains(item)) {
            return false;
        }
        if (copyOnNew) {
            items = new HashSet<>(items);
        }
        return add(item);
    }

    public void addAll(LR0ItemSet set) {
        items.addAll(set.items);
    }

    public LR0ItemSet findNextIs(Symbol s) {
        Set<LR0Item> data = nextSymbolTable.get(s);
        if (data == null) {
            data = new HashSet<>();
            for (LR0Item item : items) {
                if (item.pointNext() == s) {
                    data.add(item);
                }
            }
            nextSymbolTable.put(s, data);
        }
        return new LR0ItemSet(data);
    }

    @Override
    public Iterator<LR0Item> iterator() {
        return items.iterator();
    }

    public LR0ItemSet findReducible() {
        return new LR0ItemSet(
            items.stream()
                .filter(LR0Item::isReducible)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof LR0ItemSet) { return items.equals(((LR0ItemSet) obj).items); }
        return false;
    }

    @Override
    public String toString() {
        return items.toString();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

}
