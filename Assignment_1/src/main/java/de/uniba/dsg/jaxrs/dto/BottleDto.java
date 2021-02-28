package de.uniba.dsg.jaxrs.dto;

import de.uniba.dsg.jaxrs.model.Bottle;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.*;
import java.net.*;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="bottle")
@XmlType(propOrder = {"id", "name", "volume", "isAlcoholic", "volumePercent", "price",  "supplier", "inStock", "href"})
public class BottleDto {

    private int id;
    private String name;
    private double volume;
    private boolean isAlcoholic;
    private double volumePercent;
    private double price;
    private String supplier;
    private int inStock;
    private URI href;

    public BottleDto() { }

    public BottleDto(Bottle bottle, URI href) {
        this.id = bottle.getId();
        this.name = bottle.getName();
        this.volume = bottle.getVolume();
        this.isAlcoholic = bottle.isAlcoholic();
        this.volumePercent = bottle.getVolumePercent();
        this.price = bottle.getPrice();
        this.supplier = bottle.getSupplier();
        this.inStock = bottle.getInStock();
        this.href = href;
    }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public double getVolume() { return volume; }

    public boolean isAlcoholic() {
        return isAlcoholic;
    }

    public double getVolumePercent() { return volumePercent; }

    public double getPrice() {
        return price;
    }

    public String getSupplier() {
        return supplier;
    }

    public int getInStock() { return inStock; }

    public void setHref(URI href) { this.href = href; }


    public static BottleDto getForCustomerWithUri(UriInfo info, Bottle bottle) throws URISyntaxException {
        URI bottleUri = new URI(info.getBaseUri() + "customer/bottles/" + bottle.getId());
        return new BottleDto(bottle, bottleUri);
    }

    public static List<BottleDto> getListForCustomerWithUri(UriInfo info, List<Bottle> bottles) {
        List<BottleDto> result = new ArrayList<>();

        for (Bottle bottle : bottles) {
            try {
                result.add(getForCustomerWithUri(info, bottle));
            } catch (URISyntaxException ignored) { }
        }

        return result;
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
