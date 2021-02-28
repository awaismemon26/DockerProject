package de.uniba.dsg.jaxrs.model;

public class OrderItem {

    private int number;
    private Beverage beverage;
    private int quantity;

    public OrderItem(int number, Beverage beverage, int quantity) {
        this.number = number;
        this.beverage = beverage;
        this.quantity = quantity;
    }

    public int getNumber() {
        return number;
    }

    public Beverage getBeverage() {
        return beverage;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "number=" + number +
                ", beverage=" + beverage +
                ", quantity=" + quantity +
                '}';
    }
}
