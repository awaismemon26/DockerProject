package de.uniba.dsg.jaxrs.dto;

import de.uniba.dsg.jaxrs.model.Bottle;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "order")
@XmlType(propOrder = {"number", "bottleId", "quantity"})
public class BottleOrderDto {

    private int number;
    private int bottleId;
    private int quantity;

    public BottleOrderDto() {
    }

    public BottleOrderDto(int number, Bottle bottle, int quantity) {
        this.number = number;
        this.bottleId = bottle.getId();
        this.quantity = quantity;
    }

    public int getBottleId() {
        return bottleId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "BottleOrderDto{" +
                "number=" + number +
                ", bottleId=" + bottleId +
                ", quantity=" + quantity +
                '}';
    }
}
