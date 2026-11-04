package restaurant;

public abstract class Customer {
    private final int id;
    private int satisfaction = GameConfig.START_SATISFACTION;
    private CustomerState state = CustomerState.WAITING_FOR_TABLE;
    private Table table;
    private Order order;
    private int servedOnTurn = -1;
    private int readyToPayOnTurn = -1;

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

    /**
     * Deterministic food choice (documented rule): customer number n picks
     * Salad, Burger, Pasta, Salad, Burger, Pasta, ... i.e. menu item ((n - 1) mod 3).
     */
    public Dish chooseDish() {
        MenuItem[] items = MenuItem.values();
        return items[(id - 1) % items.length];
    }

    public int getId()                 { return id; }
    public int getSatisfaction()       { return satisfaction; }
    public CustomerState getState()    { return state; }
    public Table getTable()            { return table; }
    public Order getOrder()            { return order; }
    public int getReadyToPayOnTurn()   { return readyToPayOnTurn; }
    public boolean wasServed()         { return servedOnTurn >= 0; }

    /** True while waiting, seated or ordered (i.e. not yet served): these lose satisfaction. */
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

    // ---- State transitions ----
    public void seatAt(Table t)        { table = t; state = CustomerState.SEATED; }
    public void receiveOrder(Order o)  { order = o; state = CustomerState.ORDERED; }
    public void markServed(int turn)   { servedOnTurn = turn; state = CustomerState.EATING; }

    /** A customer served on an EARLIER turn finishes eating and becomes READY_TO_PAY. */
    public void finishEatingIfDue(int turn) {
        if (state == CustomerState.EATING && servedOnTurn < turn) {
            state = CustomerState.READY_TO_PAY;
            readyToPayOnTurn = turn;
        }
    }

    public void markPaid()             { state = CustomerState.PAID; }
}
