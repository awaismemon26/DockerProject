package de.uniba.dsg.jaxrs.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="bottle")
@XmlType(propOrder = {"id", "name", "volume", "isAlcoholic", "volumePercent", "price",  "supplier", "inStock", "href"})
public class BottleDto {

    private int id;

    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private double volume;

    @XmlElement(required = true)
    private boolean isAlcoholic;

    @XmlElement(required = true)
    private double volumePercent;

    @XmlElement(required = true)
    private double price;

    @XmlElement(required = true)
    private String supplier;

    @XmlElement(required = true)
    private int inStock;

    private String href;

    public BottleDto() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isAlcoholic() {
        return isAlcoholic;
    }

    public void setAlcoholic(boolean alcoholic) {
        isAlcoholic = alcoholic;
    }

    public double getVolumePercent() {
        return volumePercent;
    }

    public void setVolumePercent(double volumePercent) {
        this.volumePercent = volumePercent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
        return "BottleDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isAlcoholic=" + isAlcoholic +
                ", price=" + price +
                ", supplier='" + supplier + '\'' +
                ", href=" + href +
                '}';
    }

}
