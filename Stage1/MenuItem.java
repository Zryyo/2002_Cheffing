package Stage1;

public class MenuItem {
    public enum items { SALAD, PASTA, BRUGER}
    private String name;
    private double price;
    private int units;
    public String getName(){
        return this.name;
    }
    public double getPrice() {
        return this.price;
    }
    public int getUnits(){
        return this.units;
    }
}
