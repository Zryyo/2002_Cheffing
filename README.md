
# SC2002 Restaurant Rush

A turn-based restaurant management game in Java with a command-line interface.
You play the manager. Staff time is limited, so you decide which customers and orders come first.

- **Repository:** Insert REPO_URL
- **Stages (git tags):** `stage-1`, `stage-2`, `stage-3`

## Build and run

Requires a JDK (Java 17 or newer). From the project folder:

```
mkdir -p out
javac -d out src/*.java
java -cp out restaurant.PlayStage
```

Type the number of your choice at each prompt. Press Ctrl+C to quit.

To replay the reproducible test runs (see [Testing](#testing)):

```
java -cp out restaurant.PlayStage < tests/win_input.txt
java -cp out restaurant.PlayStage < tests/lose_input.txt
```

## Game setup

Your game must always offer this base setting, so that a TA can reproduce your runs. Other difficulty settings are optional.

| **Setting**   | **Required base value**                                                                                                                                  |
| ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Turns**     | 18 turns maximum                                                                                                                                               |
| **Tables**    | 2                                                                                                                                                              |
| **Staff**     | Waiter × 1 (one task per turn)<br>Chef × 1 (one preparation unit per turn)<br>Cashier × 1 (one payment per turn)                                                    |
| **Arrivals**  | One customer at the start of turns 1, 4, 7, 10, 13 and 16 (six in total)                                                                                       |
| **Objective** | At the end of turn 18: **at least 4 paid customers** AND revenue **at least $45** AND average satisfaction of paid customers **at least 60** |

| **Menu item** | **Price** | **Preparation units** |
| ------------------- | --------------- | --------------------------- |
| **Salad**     | $9.00           | 1                           |
| **Burger**    | $15.00          | 1                           |
| **Pasta**     | $18.00          | 2                           |

## How a turn works

### Start-of-turn display

At the start of every turn, show: the turn number, revenue, average satisfaction of paid customers, the waiting queue, occupied tables, each customer’s satisfaction, order status, and the staff tasks available.

### Turn steps

Each turn then runs these six steps in order.

| **Step**                       | **What happens**                                                                                                                                                                                                                                                                                                                                             |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **1  Arrivals**                | The scheduled customer joins the waiting queue with satisfaction 100.                                                                                                                                                                                                                                                                                              |
| **2  Manager choices**         | **Waiter:** choose at most one task, or Wait: seat one queued customer at a free table, take one seated customer’s order, or serve one READY order. **Chef:** choose at most one order to advance by one preparation unit, or Wait. The Chef may work on an order the Waiter took this turn.                                                          |
| **3  Kitchen and serving**     | The Waiter acts before the Chef. When an order’s last preparation unit is done, it becomes READY. A dish can be served only if it was already READY when the Waiter acts, so a dish finished this turn is served next turn at the earliest. Serving sets it to SERVED.                                                                                            |
| **4  Eating and payment**      | A customer served on an earlier turn finishes one full turn of eating and becomes READY_TO_PAY. The Cashier automatically handles at most one READY_TO_PAY customer per turn, oldest first. A customer who became READY_TO_PAY this turn pays next turn at the earliest. Payment adds the bill to revenue, marks the customer and order PAID, and frees the table. |
| **5  Waiting and abandonment** | At turn end, every waiting or seated customer who has not been served loses satisfaction (Regular: 8 points, minimum 0). A customer who reaches 0 leaves without paying: cancel any unfinished order and free the table. Customers who are served, eating or ready to pay lose nothing.                                                                            |
| **6  Result**                  | The game always runs all 18 turns; there is no early ending. After turn 18: **Victory** if all three objectives are met, otherwise **Defeat**.                                                                                                                                                                                                         |

## Design decisions

- **Customers choose their own food.** Deterministic rule: customer number *n* orders menu item *(n - 1) mod 3*. Customers 1 to 6 therefore order Salad, Burger, Pasta, Salad, Burger, Pasta. The choice is shown when the order is placed.
- **Money is stored as integer cents** and displayed with two decimals.
- **Rules stay out of the UI.** `GameScreen` only shows state and collects choices. Rules live in the domain classes (`Customer`, `Order`, `Waiter`, `Chef`, `Cashier`, `Restaurant`) and the control class (`Game`).
- **Waiter, then Chef.** The Waiter's valid action takes effect immediately, then the Chef is asked. This lets the Chef work on an order the Waiter took in the same turn.
- **Invalid choices.** A failed action prints the reason and asks again. The staff task is not used up.
- **Single `src` folder with git tags.** Each stage is a tagged commit on `main`, not a copy of the code.

## Project structure (Stage 1)

| File                                                 | Responsibility                                                   |
| ---------------------------------------------------- | ---------------------------------------------------------------- |
| `PlayStage`                                        | Entry point: builds the game and starts it                       |
| `Game`                                             | Runs the 18 turns and the six steps; decides victory or defeat   |
| `GameScreen`                                       | Command-line screen: displays state, reads the manager's choices |
| `GameConfig`                                       | Base-setting numbers (turns, tables, arrival turns, win targets) |
| `Restaurant`                                       | Game state: tables, queue, customers, orders, staff, ledger      |
| `Customer`, `RegularCustomer`, `CustomerState` | Customer behaviour and lifecycle                                 |
| `Order`, `OrderStatus`                           | One order and its preparation progress                           |
| `Dish`, `MenuItem`                               | What can be ordered and its price in cents                       |
| `Staff`, `Waiter`, `Chef`, `Cashier`         | Staff roles, where each enforces its own one-task-per-turn rule  |
| `Table`                                            | A table and its occupant                                         |
| `Ledger`                                           | Revenue and satisfaction recorded at payment                     |
| `ArrivalSchedule`                                  | Which customer arrives on which turn                             |
| `ActionResult`, `WaiterAction`, `ChefAction`   | Results and choices passed between the screen and the game       |

## Testing

Reproducible runs are in `tests/` (details in `tests/TEST_NOTES.md`):

| Run     | Input file               | Result                                    |
| ------- | ------------------------ | ----------------------------------------- |
| Victory | `tests/win_input.txt`  | 5 paid, $66.00, average satisfaction 79.2 |
| Defeat  | `tests/lose_input.txt` | 0 paid, $0.00, average satisfaction N/A   |

Expected full output is stored in `tests/win_output.txt` and `tests/lose_output.txt`.

## Stage comparison

### Stage 1 (tag `stage-1`)

Basic restaurant operation: Regular customers, menu, Waiter, Chef, Cashier, Table, Order, waiting queue, the full turn sequence, victory and defeat.

### Stage 1 to Stage 2 (VIP and Critic customers)

- **Compare:** Insert REPO_URL/compare/stage-1...stage-2
- **New files:** _TODO_
- **Changed or deleted files and why:** _TODO_

### Stage 2 to Stage 3 (Host, ComboMeal, Happy Hour)

- **Compare:** Insert REPO_URL/compare/stage-2...stage-3
- **New files:** _TODO_
- **Changed or deleted files and why:** _TODO_

## Video timestamps

| # | Segment                                                                      | Timestamp | Presenter |
| - | ---------------------------------------------------------------------------- | --------- | --------- |
| 1 | The game launching from the submitted project                                | _TODO_  | _TODO_  |
| 2 | Meaningful manager choices, including a decision where staff are constrained | _TODO_  | _TODO_  |
| 3 | A dish going READY → SERVED → payment                                  | _TODO_  | _TODO_  |
| 4 | A VIP or Critic behaviour                                                    | _TODO_  | _TODO_  |
| 5 | A Stage 3 feature                                                            | _TODO_  | _TODO_  |
| 6 | One victory and one defeat                                                   | _TODO_  | _TODO_  |
| 7 | The stage tag comparisons and the diagrams                                   | _TODO_  | _TODO_  |
