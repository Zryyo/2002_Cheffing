package restaurant;

/**
 * The Cashier works automatically: at most ONE payment per turn, oldest READY_TO_PAY first.
 * A customer who became READY_TO_PAY this turn is paid next turn at the earliest.
 */
public class Cashier extends Staff {
    @Override
    public String getRole() {
        return "Cashier";
    }

    /** Returns the customer who paid, or null if nobody could be charged this turn. */
    public Customer collectPayment(Restaurant r, int turn) {
        if (hasActed()) return null;

        Customer next = null;
        for (Customer c : r.getCustomers()) {
            if (c.getState() == CustomerState.READY_TO_PAY && c.getReadyToPayOnTurn() < turn) {
                if (next == null || c.getReadyToPayOnTurn() < next.getReadyToPayOnTurn()) {
                    next = c;
                }
            }
        }
        if (next == null) return null;

        Order order = next.getOrder();
        order.markPaid();
        next.markPaid();
        next.getTable().release();
        r.getLedger().recordPayment(order.getBillCents(), next.getSatisfaction());
        markActed();
        return next;
    }
}
