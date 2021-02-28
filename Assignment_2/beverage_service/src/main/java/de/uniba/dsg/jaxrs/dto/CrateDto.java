package de.uniba.dsg.jaxrs.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="crate")
@XmlType(propOrder = {"id", "bottle", "noOfBottles", "price", "inStock", "href"})

public class CrateDto {

    private int id;

    @XmlElement(required = true)
    private BottleDto bottle;

    @XmlElement(required = true)
    private int noOfBottles;

    @XmlElement(required = true)
    private double price;

    @XmlElement(required = true)
    private int inStock;

    private String href;

    public CrateDto() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BottleDto getBottle() {
        return bottle;
    }

    public void setBottle(BottleDto bottle) {
        this.bottle = bottle;
    }

    public int getNoOfBottles() {
        return noOfBottles;
    }

    public void setNoOfBottles(int noOfBottles) {
        this.noOfBottles = noOfBottles;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "CrateDto{" +
                "id=" + id +
                ", bottle=" + bottle +
                ", noOfBottles=" + noOfBottles +
                ", price=" + price +
                ", inStock=" + inStock +
                ", href=" + href +
                '}';
    }
}
