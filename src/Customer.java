package restaurant;

public abstract class Customer {
    private final int id;
    private int satisfaction = 100;
    private CustomerState state = CustomerState.WAITING_FOR_TABLE;

    protected Customer(int id) {
        this.id = id;
    }

    /** Points lost per turn while waiting, seated or ordered but not yet served. */
    protected abstract int waitLossPerTurn();

    public abstract String getTypeName();

    /** Bill for a dish. Subclasses (e.g. VIP) can override for a discount. */
    public int billFor(Dish dish) {
        return dish.getPriceCents();
    }

    public int getId()                 { return id; }
    public int getSatisfaction()       { return satisfaction; }
    public CustomerState getState()    { return state; }

    public boolean isAwaitingService() {
        return state == CustomerState.WAITING_FOR_TABLE
            || state == CustomerState.SEATED
            || state == CustomerState.ORDERED;
    }

    /** End-of-turn loss. A customer who reaches 0 leaves. */
    public void applyWaitingLoss() {
        if (!isAwaitingService()) return;
        satisfaction = Math.max(0, satisfaction - waitLossPerTurn());
        if (satisfaction == 0) state = CustomerState.LEFT;
    }

    // State transitions
    public void markSeated()       { state = CustomerState.SEATED; }
    public void markOrdered()      { state = CustomerState.ORDERED; }
    public void markEating()       { state = CustomerState.EATING; }
    public void markReadyToPay()   { state = CustomerState.READY_TO_PAY; }
    public void markPaid()         { state = CustomerState.PAID; }
}