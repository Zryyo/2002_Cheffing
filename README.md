2002 Group Project - playable restaurant management game in Java

The player is the manager. Staff time is limited, so the manager decides which customers and orders come first. You build the game in three stages, and each stage must show how your object-oriented design supports the next set of requirements.

## General rules
- Customers choose their own food. The manager never chooses it. Use a documented deterministic rule or seeded randomness, and show the choice once the order is placed.
- Keep rules out of the UI. The UI only collects the manager’s decisions and displays the state. Game rules belong in domain and control objects.
- Money. If fractional prices arise, store amounts as integer cents. Display dollars with two
decimals.
- Offline. All mandatory features must work without network access

### How a turn works
Start-of-turn display
At the start of every turn, show: the turn number, revenue, average satisfaction of paid customers, the
waiting queue, occupied tables, each customer’s satisfaction, order status, and the staff tasks
available.

### Turn Steps
Each turn then runs these six steps in order.
Step What happens
1 Arrivals The scheduled customer joins the waiting queue with satisfaction 100.
2 Manager choices Waiter: choose at most one task, or Wait: seat one queued customer at a free table,
take one seated customer’s order, or serve one READY order.
Chef: choose at most one order to advance by one preparation unit, or Wait. The
Chef may work on an order the Waiter took this turn.
3 Kitchen and serving The Waiter acts before the Chef. When an order’s last preparation unit is done, it
becomes READY. A dish can be served only if it was already READY when the Waiter
acts, so a dish finished this turn is served next turn at the earliest. Serving sets it to
SERVED.
4 Eating and payment A customer served on an earlier turn finishes one full turn of eating and becomes
READY_TO_PAY. The Cashier automatically handles at most one READY_TO_PAY
customer per turn, oldest first. A customer who became READY_TO_PAY this turn
pays next turn at the earliest. Payment adds the bill to revenue, marks the customer
and order PAID, and frees the table.
5 Waiting and
abandonment
At turn end, every waiting or seated customer who has not been served loses
satisfaction (Regular: 8 points, minimum 0). A customer who reaches 0 leaves
without paying: cancel any unfinished order and free the table. Customers who are
served, eating or ready to pay lose nothing.