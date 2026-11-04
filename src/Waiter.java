package restaurant;

/** The Waiter does ONE task per turn: seat a customer, take an order, or serve a READY order. */
public class Waiter extends Staff {
    @Override
    public String getRole() {
        return "Waiter";
    }

    public ActionResult seat(Restaurant r, Customer c, Table t) {
        if (hasActed()) return ActionResult.fail("The Waiter has already done a task this turn.");
        if (c == null) return ActionResult.fail("There is no customer waiting for a table.");
        if (t == null) return ActionResult.fail("There is no free table.");
        if (c.getState() != CustomerState.WAITING_FOR_TABLE) {
            return ActionResult.fail("Customer #" + c.getId() + " is not waiting for a table.");
        }
        if (!t.isFree()) return ActionResult.fail("Table " + t.getNumber() + " is already occupied.");

        t.assign(c);
        c.seatAt(t);
        r.removeFromQueue(c);
        markActed();
        return ActionResult.ok("Waiter seats Customer #" + c.getId() + " at Table " + t.getNumber() + ".");
    }

    public ActionResult takeOrder(Restaurant r, Customer c) {
        if (hasActed()) return ActionResult.fail("The Waiter has already done a task this turn.");
        if (c == null) return ActionResult.fail("No seated customer is waiting to order.");
        if (c.getState() != CustomerState.SEATED) {
            return ActionResult.fail("Customer #" + c.getId() + " is not waiting to order.");
        }

        Dish dish = c.chooseDish();                 // the customer chooses, not the manager
        int bill = c.billFor(dish);                 // bill is locked now
        Order order = new Order(c, dish, bill);
        c.receiveOrder(order);
        r.addOrder(order);
        markActed();
        return ActionResult.ok("Customer #" + c.getId() + " orders " + dish.getName()
                + " (" + Ledger.formatCents(bill) + ", " + dish.getUnits() + " prep unit(s)).");
    }

    public ActionResult serve(Restaurant r, Order o, int turn) {
        if (hasActed()) return ActionResult.fail("The Waiter has already done a task this turn.");
        if (o == null) return ActionResult.fail("There is no READY order to serve.");
        if (o.getStatus() != OrderStatus.READY) {
            return ActionResult.fail("That order is not READY yet (status: " + o.getStatus() + ").");
        }

        o.markServed();
        o.getCustomer().markServed(turn);
        markActed();
        return ActionResult.ok("Waiter serves " + o.getDish().getName()
                + " to Customer #" + o.getCustomer().getId() + ".");
    }
}
