package lab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Typical Java 8 idioms from an order-report class, written the way 8 forced you to. */
public class Legacy8 {
    static String label(Object status) {
        if (status instanceof String) {
            String s = (String) status;
            if (s.equals("PAID")) return "Paid";
            else if (s.equals("SHIPPED")) return "Shipped";
            return "Other";
        }
        return "Unknown";
    }

    static List<String> skus() {
        List<String> l = new ArrayList<String>();
        l.add("book");
        l.add("pen");
        return Collections.unmodifiableList(l);
    }

    static String report(List<String> skus) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"skus\": ").append(skus).append("\n");
        sb.append("}");
        return sb.toString();
    }

    static String firstOrEmpty(List<String> l) {
        return l.isEmpty() ? "" : l.get(0);
    }
}
