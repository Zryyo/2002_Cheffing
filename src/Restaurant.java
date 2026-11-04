package restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the whole game state: tables, waiting queue, customers, orders, staff and money.
 * Other classes ask it questions (get...) instead of reaching into its lists.
 */
public class Restaurant {
    private final List<Table> tables = new ArrayList<>();
    private final List<Customer> queue = new ArrayList<>();      // waiting for a table
    private final List<Customer> customers = new ArrayList<>();  // everyone who has arrived
    private final List<Order> orders = new ArrayList<>();
    private final Ledger ledger = new Ledger();
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();
    private int unhappyDepartures;

    public Restaurant() {
        for (int i = 1; i <= GameConfig.NUM_TABLES; i++) {
            tables.add(new Table(i));
        }
    }

    // ---------- changes ----------
    public void startTurn() {
        waiter.resetForNewTurn();
        chef.resetForNewTurn();
        cashier.resetForNewTurn();
    }

    public void addArrival(Customer c) {
        queue.add(c);
        customers.add(c);
    }

    public void removeFromQueue(Customer c) { queue.remove(c); }
    public void addOrder(Order o)           { orders.add(o); }

    /** Step 4 (first half): customers served on an earlier turn become READY_TO_PAY. */
    public void advanceEating(int turn) {
        for (Customer c : customers) {
            c.finishEatingIfDue(turn);
        }
    }

    /** Step 5: unserved customers lose satisfaction; those at 0 leave. Returns who left. */
    public List<Customer> applyEndOfTurnWaiting() {
        List<Customer> departed = new ArrayList<>();
        for (Customer c : customers) {
            if (c.isAwaitingService()) {
                c.applyWaitingLoss();
                if (c.getState() == CustomerState.LEFT) {
                    removeAbandoned(c);
                    departed.add(c);
                }
            }
        }
        return departed;
    }

    private void removeAbandoned(Customer c) {
        queue.remove(c);
        if (c.getOrder() != null) {
            c.getOrder().cancel();
        }
        if (c.getTable() != null) {
            c.getTable().release();
        }
        unhappyDepartures++;
    }

    // ---------- questions ----------
    public List<Customer> getQueue() { return new ArrayList<>(queue); }

    public List<Table> getFreeTables() {
        List<Table> free = new ArrayList<>();
        for (Table t : tables) {
            if (t.isFree()) free.add(t);
        }
        return free;
    }

    public List<Customer> getSeatedWithoutOrder() {
        List<Customer> result = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getState() == CustomerState.SEATED) result.add(c);
        }
        return result;
    }

    public List<Order> getReadyOrders() {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.READY) result.add(o);
        }
        return result;
    }

    /** Orders the Chef can still work on (taken or part-prepared). */
    public List<Order> getCookableOrders() {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.canAdvance()) result.add(o);
        }
        return result;
    }

    public int getServedCount() {
        int n = 0;
        for (Customer c : customers) {
            if (c.wasServed()) n++;
        }
        return n;
    }

    public int getUnhappyDepartures() { return unhappyDepartures; }
    public List<Table> getTables()    { return tables; }
    public List<Customer> getCustomers() { return customers; }
    public Ledger getLedger()         { return ledger; }
    public Waiter getWaiter()         { return waiter; }
    public Chef getChef()             { return chef; }
    public Cashier getCashier()       { return cashier; }
}
