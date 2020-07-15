package grammar.ui;

import com.google.common.collect.Table;
import grammar.Rule;
import grammar.Symbol;

import java.util.Stack;

public class Printer {

    public static void printLLTable(Table<Symbol, Symbol, Rule> table) {
        StringBuilder builder = new StringBuilder(getFormat(""));
        for (var col : table.columnKeySet()) {
            builder.append(getFormat(col.toString()));
        }
        for (var row : table.rowMap().entrySet()) {
            builder.append("\n");

            builder.append(getFormat(row.getKey().toString()));
            for (var v : row.getValue().entrySet()) {
                builder.append(getFormat(v.toString().substring(2)));
            }
        }
        System.out.println(builder.toString());
    }

    public static void printStack(Iterable stack) {
        StringBuilder builder = new StringBuilder();
        for (var s : stack) {
            builder.append(s);
        }
        System.out.print(getFormat(builder.toString(), 30));
    }


    public static void printTitle() {
        System.out.println(getFormat("Input", 30) + getFormat("Stack", 30) + "Output");
    }

    public static String getFormat(String x) {
        return getFormat(x, 15);
    }

    public static String getFormat(String x, int length) {
        if (x == null) {
            x = "null";
        }
        return String.format("%-" + length + "s", x);
    }

}
