# Stage 1 test notes

Game rule documented here for reproducibility:
**Food choice (deterministic):** customer number n always orders menu item ((n - 1) mod 3):
Customer #1 Salad, #2 Burger, #3 Pasta, #4 Salad, #5 Burger, #6 Pasta.
Arrivals: one Regular customer at the start of turns 1, 4, 7, 10, 13, 16 (ids 1 to 6).

## How to run
From the project folder:

    javac -d out src/*.java
    java -cp out restaurant.PlayStage                      # play by hand
    java -cp out restaurant.PlayStage < tests/win_input.txt   # replay the winning run
    java -cp out restaurant.PlayStage < tests/lose_input.txt  # replay the losing run

`tests/win_output.txt` and `tests/lose_output.txt` are the expected full outputs
(compare with `java -cp out restaurant.PlayStage < tests/win_input.txt | diff - tests/win_output.txt`).

## Run 1: Victory (tests/win_input.txt)
Strategy: seat each customer on arrival, take the order the next turn, have the Chef cook it
immediately, serve as soon as it is READY. When the Waiter has to choose between serving and
seating (turns 10 and 13), serve first so the served customer stops losing satisfaction.

| Customer | Dish | Paid on turn | Bill | Satisfaction recorded |
|---|---|---|---|---|
| #1 | Salad | 5 | $9.00 | 84 |
| #2 | Burger | 8 | $15.00 | 84 |
| #3 | Pasta | 12 | $18.00 | 76 |
| #4 | Salad | 15 | $9.00 | 76 |
| #5 | Burger | 18 | $15.00 | 76 |

Result: 5 paid, revenue $66.00, average satisfaction 79.2 -> **VICTORY**.
(Customer #6 arrives on turn 16 and cannot finish paying in time; this is intended by the brief.)

## Run 2: Defeat (tests/lose_input.txt)
Strategy: the manager chooses Wait for both staff on every turn.
Customers #1 and #2 lose 8 per turn and leave (turns 13 and 16); nobody pays.

Result: 0 paid, revenue $0.00, average satisfaction N/A, 2 unhappy departures -> **DEFEAT**.

## Check against the brief's worked example (Section 3.3)
One Regular customer ordering Pasta with full attention gives satisfaction
92 / 84 / 76 / 76 / 76, served on turn 4 and paid $18.00 on turn 6 with 76 recorded.
The game reproduces this exactly.

## Other checks done
- Invalid menu input (letters, out-of-range numbers) is rejected and asked again.
- Serving with nothing READY, taking an order with nobody seated, and advancing with an
  empty kitchen all print the reason and do NOT use up the staff task.
- If input ends early the game stops with a message instead of crashing.
