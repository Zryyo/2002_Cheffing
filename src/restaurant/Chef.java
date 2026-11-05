package restaurant;

/** The Chef advances ONE order by one preparation unit per turn. */
public class Chef extends Staff {
    @Override
    public String getRole() {
        return "Chef";
    }

    public ActionResult advance(Order o) {
        if (hasActed()) return ActionResult.fail("The Chef has already worked this turn.");
        if (o == null) return ActionResult.fail("There is no order to prepare.");
        if (!o.canAdvance()) {
            return ActionResult.fail("That order cannot be prepared (status: " + o.getStatus() + ").");
        }

        o.advancePrep();
        markActed();
        if (o.getStatus() == OrderStatus.READY) {
            return ActionResult.ok("Chef finishes " + o.getDish().getName() + " for Customer #"
                    + o.getCustomer().getId() + ": READY (can be served next turn at the earliest).");
        }
        return ActionResult.ok("Chef prepares " + o.getDish().getName() + " for Customer #"
                + o.getCustomer().getId() + " (" + o.getRemainingUnits() + " unit(s) left).");
    }
}
