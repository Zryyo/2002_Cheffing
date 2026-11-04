package restaurant;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Command-line screen. It only shows the game state and collects the manager's choices.
 * It contains NO game rules.
 */
public class GameScreen {
    private final Scanner in;
    private final PrintStream out;

    public GameScreen(InputStream input, PrintStream out) {
        this.in = new Scanner(input);
        this.out = out;
    }

    // ---------------- display ----------------

    public void showMessage(String message) {
        out.println("  >> " + message);
    }

    public void showTurnStart(int turn, Restaurant r) {
        Ledger ledger = r.getLedger();
        List<Customer> queue = r.getQueue();

        out.println();
        out.println("==================== TURN " + turn + " of " + GameConfig.MAX_TURNS + " ====================");
        out.println("Revenue: " + Ledger.formatCents(ledger.getRevenueCents())
                + "    Avg satisfaction (paid customers): " + averageText(ledger));

        out.println("Waiting queue:" + (queue.isEmpty() ? " (empty)" : ""));
        for (Customer c : queue) {
            out.println("   " + describe(c));
        }

        out.println("Tables:");
        for (Table t : r.getTables()) {
            if (t.isFree()) {
                out.println("   Table " + t.getNumber() + ": free");
            } else {
                out.println("   Table " + t.getNumber() + ": " + describe(t.getOccupant()));
            }
        }

        out.println("Staff tasks available:");
        out.println("   Waiter (1 task): seat a customer [" + queue.size() + " queued, "
                + r.getFreeTables().size() + " free table(s)], take an order ["
                + r.getSeatedWithoutOrder().size() + " seated], serve ["
                + r.getReadyOrders().size() + " READY]");
        out.println("   Chef (1 unit):   advance an order [" + r.getCookableOrders().size() + " in the kitchen]");
        out.println("   Cashier:         automatic, one payment per turn");
    }

    public void showEndOfTurn(int turn, Restaurant r) {
        Ledger ledger = r.getLedger();
        out.println("--- End of turn " + turn + " ---");
        out.println("Served: " + r.getServedCount()
                + " | Unhappy departures: " + r.getUnhappyDepartures()
                + " | Revenue: " + Ledger.formatCents(ledger.getRevenueCents())
                + " | Avg satisfaction (paid): " + averageText(ledger)
                + " | Turns used: " + turn + "/" + GameConfig.MAX_TURNS);
    }

    public void showFinalResult(GameResult result, Restaurant r) {
        Ledger ledger = r.getLedger();
        out.println();
        out.println("==================== GAME OVER ====================");
        out.println("Paid customers:  " + ledger.getPaidCount()
                + "   (need at least " + GameConfig.MIN_PAID_CUSTOMERS + ")");
        out.println("Revenue:         " + Ledger.formatCents(ledger.getRevenueCents())
                + "   (need at least " + Ledger.formatCents(GameConfig.MIN_REVENUE_CENTS) + ")");
        out.println("Avg satisfaction: " + averageText(ledger)
                + "   (need at least " + GameConfig.MIN_AVG_SATISFACTION + ")");
        out.println("RESULT: " + result);
    }

    // ---------------- input ----------------

    public WaiterAction promptWaiterAction(Restaurant r) {
        out.println();
        out.println("WAITER - choose ONE task:");
        out.println("  1) Seat a queued customer");
        out.println("  2) Take an order");
        out.println("  3) Serve a READY order");
        out.println("  0) Wait (do nothing)");
        int choice = readInt("Choose 0-3: ", 0, 3);

        switch (choice) {
            case 1: {
                Customer c = pick("Customer to seat", r.getQueue(), this::describe);
                Table t = (c == null) ? null
                        : pick("Table", r.getFreeTables(), table -> "Table " + table.getNumber());
                return new WaiterAction(WaiterAction.Type.SEAT, c, t, null);
            }
            case 2: {
                Customer c = pick("Customer whose order to take", r.getSeatedWithoutOrder(), this::describe);
                return new WaiterAction(WaiterAction.Type.TAKE_ORDER, c, null, null);
            }
            case 3: {
                Order o = pick("Order to serve", r.getReadyOrders(), this::describeOrder);
                return new WaiterAction(WaiterAction.Type.SERVE, null, null, o);
            }
            default:
                return new WaiterAction(WaiterAction.Type.WAIT, null, null, null);
        }
    }

    public ChefAction promptChefAction(Restaurant r) {
        out.println();
        out.println("CHEF - choose ONE task:");
        out.println("  1) Advance an order by one preparation unit");
        out.println("  0) Wait (do nothing)");
        int choice = readInt("Choose 0-1: ", 0, 1);

        if (choice == 1) {
            Order o = pick("Order to prepare", r.getCookableOrders(), this::describeOrder);
            return new ChefAction(ChefAction.Type.ADVANCE, o);
        }
        return new ChefAction(ChefAction.Type.WAIT, null);
    }

    // ---------------- helpers ----------------

    private String averageText(Ledger ledger) {
        return ledger.hasPaidCustomers()
                ? String.format("%.1f", ledger.getAverageSatisfaction())
                : "N/A";
    }

    private String describe(Customer c) {
        String s = "#" + c.getId() + " " + c.getTypeName()
                + " | satisfaction " + c.getSatisfaction()
                + " | " + c.getState();
        Order o = c.getOrder();
        if (o != null) {
            s += " | order: " + o.getDish().getName() + " " + o.getStatus();
            if (o.canAdvance()) {
                s += " (" + o.getRemainingUnits() + " unit(s) left)";
            }
        }
        return s;
    }

    private String describeOrder(Order o) {
        return o.getDish().getName() + " for Customer #" + o.getCustomer().getId()
                + " [" + o.getStatus() + ", " + o.getRemainingUnits() + " unit(s) left]";
    }

    /** Shows a numbered list. Returns null if empty; auto-selects if there is only one option. */
    private <T> T pick(String title, List<T> options, Function<T, String> label) {
        if (options.isEmpty()) {
            return null;
        }
        if (options.size() == 1) {
            out.println("  " + title + ": " + label.apply(options.get(0)) + "  (only choice)");
            return options.get(0);
        }
        out.println("  " + title + ":");
        for (int i = 0; i < options.size(); i++) {
            out.println("    " + (i + 1) + ") " + label.apply(options.get(i)));
        }
        int n = readInt("  Choose 1-" + options.size() + ": ", 1, options.size());
        return options.get(n - 1);
    }

    /** Keeps asking until the manager types a whole number in range. */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            out.print(prompt);
            out.flush();
            String line = in.nextLine().trim();   // throws NoSuchElementException if input has ended
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            out.println("  Invalid input. Please type a whole number from " + min + " to " + max + ".");
        }
    }
}
