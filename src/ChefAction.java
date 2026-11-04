package restaurant;

/** What the manager chose for the Chef this turn. */
public record ChefAction(Type type, Order order) {
    public enum Type { ADVANCE, WAIT }
}
