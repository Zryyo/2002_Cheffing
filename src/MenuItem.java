package Stage1;

public enum MenuItem {
    SALAD("Salad", 900, 1),
    BURGER("Burger", 1500, 1),
    PASTA("Pasta", 1800, 2);
    private String name;
    private double price;
    private int units;

    MenuItem(String name, int price, int units) {
    this.name = name;
        this.price = price;
        this.units = units;
    }
    public String getName() {
        return this.name;
    }
    public double getPrice() {
        return this.price;
    }
    public int getUnits() {
        return this.units;
    }
}
