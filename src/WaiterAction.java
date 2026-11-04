package restaurant;

/** What the manager chose for the Waiter this turn (collected by the UI, run by the Game). */
public record WaiterAction(Type type, Customer customer, Table table, Order order) {
    public enum Type { SEAT, TAKE_ORDER, SERVE, WAIT }
}
