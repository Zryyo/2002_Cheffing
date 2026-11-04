package restaurant;

import java.util.NoSuchElementException;

/** Entry point: builds the game and starts it. */
public class PlayStage {
    public static void main(String[] args) {
        GameScreen view = new GameScreen(System.in, System.out);
        Restaurant restaurant = new Restaurant();
        Game game = new Game(restaurant, ArrivalSchedule.allRegular(), view);
        try {
            game.run();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Input ended before the game finished. Game stopped.");
        }
    }
}
