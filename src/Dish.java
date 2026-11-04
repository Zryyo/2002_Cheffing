package restaurant;

/** Anything that can be ordered. MenuItem (and later ComboMeal) implement this. */
public interface Dish {
    String getName();
    int getPriceCents();
    int getUnits();
}

