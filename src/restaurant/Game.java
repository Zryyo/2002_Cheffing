package restaurant;

import java.util.List;
import java.util.Optional;

/**
 * Runs the game: 18 turns, each following the six steps in the brief.
 * It joins the UI, the staff and the restaurant together but keeps the rules in the domain objects.
 */
public class Game {
    private final Restaurant restaurant;
    private final ArrivalSchedule schedule;
    private final GameScreen view;

    public Game(Restaurant restaurant, ArrivalSchedule schedule, GameScreen view) {
        this.restaurant = restaurant;
        this.schedule = schedule;
        this.view = view;
    }

    /** Always plays all 18 turns (no early ending), then returns the result. */
    public GameResult run() {
        for (int turn = 1; turn <= GameConfig.MAX_TURNS; turn++) {
            runTurn(turn);
        }
        GameResult result = evaluateResult();
        view.showFinalResult(result, restaurant);
        return result;
    }

    private void runTurn(int turn) {
        restaurant.startTurn();

        stepArrivals(turn);                       // 1
        view.showTurnStart(turn, restaurant);
        stepWaiter(turn);                         // 2 + 3: Waiter acts first...
        stepChef();                               //        ...then the Chef
        stepEatingAndPayment(turn);               // 4
        stepWaitingAndAbandonment();              // 5
        view.showEndOfTurn(turn, restaurant);
    }

    // Step 1
    private void stepArrivals(int turn) {
        Optional<Customer> arriving = schedule.arrivingOn(turn);
        if (arriving.isPresent()) {
            Customer c = arriving.get();
            restaurant.addArrival(c);
            view.showMessage("Customer #" + c.getId() + " (" + c.getTypeName()
                    + ") arrives and joins the queue with satisfaction " + c.getSatisfaction() + ".");
        }
    }

    // Steps 2-3 (Waiter). An invalid choice explains why and asks again; the task is not used up.
    private void stepWaiter(int turn) {
        while (true) {
            WaiterAction action = view.promptWaiterAction(restaurant);
            if (action.type() == WaiterAction.Type.WAIT) {
                view.showMessage("Waiter waits.");
                return;
            }
            ActionResult result = executeWaiterAction(action, turn);
            view.showMessage(result.getMessage());
            if (result.isSuccess()) {
                return;
            }
        }
    }

    private ActionResult executeWaiterAction(WaiterAction action, int turn) {
        Waiter waiter = restaurant.getWaiter();
        switch (action.type()) {
            case SEAT:       return waiter.seat(restaurant, action.customer(), action.table());
            case TAKE_ORDER: return waiter.takeOrder(restaurant, action.customer());
            case SERVE:      return waiter.serve(restaurant, action.order(), turn);
            default:         return ActionResult.fail("Unknown Waiter task.");
        }
    }

    // Steps 2-3 (Chef)
    private void stepChef() {
        while (true) {
            ChefAction action = view.promptChefAction(restaurant);
            if (action.type() == ChefAction.Type.WAIT) {
                view.showMessage("Chef waits.");
                return;
            }
            ActionResult result = restaurant.getChef().advance(action.order());
            view.showMessage(result.getMessage());
            if (result.isSuccess()) {
                return;
            }
        }
    }

    // Step 4
    private void stepEatingAndPayment(int turn) {
        restaurant.advanceEating(turn);
        Customer paid = restaurant.getCashier().collectPayment(restaurant, turn);
        if (paid != null) {
            view.showMessage("Cashier collects " + Ledger.formatCents(paid.getOrder().getBillCents())
                    + " from Customer #" + paid.getId() + " (satisfaction recorded: "
                    + paid.getSatisfaction() + "). Table " + paid.getTable().getNumber() + " is free.");
        }
    }

    // Step 5
    private void stepWaitingAndAbandonment() {
        List<Customer> departed = restaurant.applyEndOfTurnWaiting();
        for (Customer c : departed) {
            view.showMessage("Customer #" + c.getId() + " ran out of patience and LEFT without paying.");
        }
    }

    // Step 6
    private GameResult evaluateResult() {
        Ledger ledger = restaurant.getLedger();
        boolean enoughPaid = ledger.getPaidCount() >= GameConfig.MIN_PAID_CUSTOMERS;
        boolean enoughRevenue = ledger.getRevenueCents() >= GameConfig.MIN_REVENUE_CENTS;
        boolean happyEnough = ledger.hasPaidCustomers()
                && ledger.getAverageSatisfaction() >= GameConfig.MIN_AVG_SATISFACTION;
        return (enoughPaid && enoughRevenue && happyEnough) ? GameResult.VICTORY : GameResult.DEFEAT;
    }
}
