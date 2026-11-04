package restaurant;

public enum MenuItem implements Dish {
    SALAD("Salad", 900, 1),
    BURGER("Burger", 1500, 1),
    PASTA("Pasta", 1800, 2);

    private final String name;
    private final int priceCents;
    private final int units;

    MenuItem(String name, int priceCents, int units) {
        this.name = name;
        this.priceCents = priceCents;
        this.units = units;
    }

    public String getName()      {
         return name; 
        }
    public int getPriceCents()   { 
        return priceCents; 
        }
    public int getUnits()        { 
        return units;
        }
}