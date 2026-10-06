package lab.orders.api;

import lab.orders.internal.TaxTable;

public final class PriceService {
    private PriceService() {}

    /** Public API: gross price in cents for a net price. */
    public static long gross(long netCents) {
        return netCents + netCents * TaxTable.VAT_PERCENT / 100;
    }
}
