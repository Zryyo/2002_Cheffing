package restaurant;

public class RegularCustomer extends Customer {
    public RegularCustomer(int id) {
        super(id);
    }

    @Override protected int waitLossPerTurn() { return 8; }
    @Override public String getTypeName()     { return "Regular"; }
}