package de.uniba.dsg.jaxrs.dto;

import de.uniba.dsg.jaxrs.model.Crate;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "order")
@XmlType(propOrder = {"number", "crateId", "quantity"})
public class CrateOrderDto {

    private int number;
    private int crateId;
    private int quantity;

    public CrateOrderDto() {
    }

    public CrateOrderDto(int number, Crate crate, int quantity) {
        this.number = number;
        this.crateId = crate.getId();
        this.quantity = quantity;
    }

    public int getCrateId() {
        return crateId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "CrateOrderDto{" +
                "number=" + number +
                ", crateId=" + crateId +
                ", quantity=" + quantity +
                '}';
    }
}
