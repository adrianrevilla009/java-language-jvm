package lab;

import java.util.List;

/** The same behaviour as Legacy8 using features added between Java 9 and 21. */
public class Modern21 {
    static String label(Object status) {
        return switch (status) { // 21: pattern matching for switch
            case String s when s.equals("PAID") -> "Paid";
            case String s when s.equals("SHIPPED") -> "Shipped";
            case String s -> "Other";
            case null, default -> "Unknown"; // without `case null` a null status throws NPE
        };
    }

    static List<String> skus() {
        return List.of("book", "pen"); // 9: immutable collection factories
    }

    static String report(List<String> skus) {
        return """
            {
              "skus": %s
            }""".formatted(skus); // 15: text blocks
    }

    static String firstOrEmpty(List<String> l) {
        return l.isEmpty() ? "" : l.getFirst(); // 21: SequencedCollection.getFirst
    }
}
