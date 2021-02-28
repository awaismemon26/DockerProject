package de.uniba.dsg.jaxrs.dto;

import de.uniba.dsg.jaxrs.model.Bottle;
import de.uniba.dsg.jaxrs.model.Crate;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="crate")
@XmlType(propOrder = {"id", "bottle", "noOfBottles", "price", "inStock", "href"})

public class CrateDto {

    private int id;
    private Bottle bottle;
    private int noOfBottles;
    private double price;
    private int inStock;
    private URI href;

    public CrateDto() { }

    public CrateDto(Crate crate, URI href) {
        this.id = crate.getId();
        this.bottle = crate.getBottle();
        this.noOfBottles = crate.getNoOfBottles();
        this.price = crate.getPrice();
        this.inStock = crate.getInStock();
        this.href = href;
    }

    public void setId(int id) { this.id = id; }

    public Bottle getBottle() {
        return bottle;
    }

    public int getNoOfBottles() {
        return noOfBottles;
    }

    public double getPrice() {
        return price;
    }

    public int getInStock() {
        return inStock;
    }

    public void setHref(URI href) { this.href = href; }

    public static CrateDto getCForCustomerWithUri(UriInfo info, Crate crate) throws URISyntaxException {
        URI crateUri = new URI(info.getBaseUri() + "customer/crates/" + crate.getId());
        return new CrateDto(crate, crateUri);
    }

    public static List<CrateDto> getCListForCustomerWithUri(UriInfo info, List<Crate> crates) {
        List<CrateDto> result = new ArrayList<>();

        for (Crate crate : crates) {
            try {
                result.add(getCForCustomerWithUri(info, crate));
            } catch (URISyntaxException ignored) { }
        }

        return result;
    }

    @Override
    public String toString() {
        return "CrateDto{" +
                "id = " + id +
                ", bottle = " + bottle +
                ", noOfBottles = " + noOfBottles +
                ", price = " + price +
                ", inStock = " + inStock +
                ", href = " + href +
                '}';
    }

}
