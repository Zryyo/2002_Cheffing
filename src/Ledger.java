package restaurant;

/** Keeps the money and the satisfaction scores recorded at payment time. */
public class Ledger {
    private int revenueCents;
    private int paidCount;
    private int satisfactionSum;

    public void recordPayment(int billCents, int satisfactionAtPayment) {
        revenueCents += billCents;
        paidCount++;
        satisfactionSum += satisfactionAtPayment;
    }

    public int getRevenueCents()    { return revenueCents; }
    public int getPaidCount()       { return paidCount; }
    public boolean hasPaidCustomers() { return paidCount > 0; }

    /** Average satisfaction of paid customers. Only meaningful if hasPaidCustomers(). */
    public double getAverageSatisfaction() {
        return paidCount == 0 ? 0.0 : (double) satisfactionSum / paidCount;
    }

    /** 900 -> "$9.00" */
    public static String formatCents(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}
