package restaurant;

public class Order {
    private final Customer customer;
    private final Dish dish;
    private final int billCents;      // locked when the order is taken
    private int remainingUnits;
    private OrderStatus status = OrderStatus.TAKEN;

    public Order(Customer customer, Dish dish, int billCents) {
        this.customer = customer;
        this.dish = dish;
        this.billCents = billCents;
        this.remainingUnits = dish.getUnits();
    }

    public Customer getCustomer()   { return customer; }
    public Dish getDish()           { return dish; }
    public int getBillCents()       { return billCents; }
    public int getRemainingUnits()  { return remainingUnits; }
    public OrderStatus getStatus()  { return status; }

    public boolean canAdvance() {
        return status == OrderStatus.TAKEN || status == OrderStatus.IN_PREPARATION;
    }

    /** Chef does one preparation unit; the order becomes READY after the last one. */
    public void advancePrep() {
        if (!canAdvance()) {
            throw new IllegalStateException("Order cannot be advanced from " + status);
        }
        remainingUnits--;
        status = (remainingUnits == 0) ? OrderStatus.READY : OrderStatus.IN_PREPARATION;
    }

    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Only a READY order can be served.");
        }
        status = OrderStatus.SERVED;
    }

    public void markPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalStateException("Only a SERVED order can be paid.");
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        status = OrderStatus.CANCELLED;
    }
}
