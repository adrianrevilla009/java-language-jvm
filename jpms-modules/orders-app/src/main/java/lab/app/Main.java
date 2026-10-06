package lab.app;

import lab.orders.api.PriceService;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("gross(10000) = " + PriceService.gross(10_000));
        Module core = PriceService.class.getModule();
        Module self = Main.class.getModule();
        System.out.println("module = " + core.getName() + " (named: " + core.isNamed() + ")");
        System.out.println("api exported to app: " + core.isExported("lab.orders.api", self));
        System.out.println("internal exported to app: " + core.isExported("lab.orders.internal", self));
        try {
            Class.forName("lab.orders.internal.TaxTable").getField("VAT_PERCENT").get(null);
            System.out.println("reflection allowed (unexpected)");
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.out.println("reflection blocked: IllegalAccessException");
        }
    }
}
