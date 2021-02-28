package de.uniba.dsg.jaxrs.dto;

import de.uniba.dsg.jaxrs.model.*;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "order")
@XmlType(propOrder = {"id", "bottles", "crates", "price", "status", "href"})
public class OrderDto {

    private int id;
    private List<BottleOrderDto> bottles;
    private List<CrateOrderDto> crates;
    private double price;
    private OrderStatus status;
    private URI href;

    // empty ctor is required because of JAX-RS
    // see https://stackoverflow.com/a/33756603
    public OrderDto() {
    }

    public OrderDto(Order order, URI href) {
        this.id  = order.getOrderId();
        this.bottles = getBottlesWithQuantityFromOrder(order);
        this.crates = getCratesWithQuantityFromOrder(order);
        this.price = order.getPrice();
        this.status = order.getStatus();
        this.href = href;
    }

    public List<BottleOrderDto> getBottles() {
        return bottles;
    }

    public List<CrateOrderDto> getCrates() {
        return crates;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBottles(List<BottleOrderDto> bottles) {
        this.bottles = bottles;
    }

    public void setCrates(List<CrateOrderDto> crates) {
        this.crates = crates;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    private List<BottleOrderDto> getBottlesWithQuantityFromOrder(Order order) {
        List<BottleOrderDto> orders = new ArrayList<>();

        order.getPositions().forEach(x -> {
            if(x.getBeverage() instanceof Bottle) {
                Bottle bottle = (Bottle) x.getBeverage();

                orders.add(new BottleOrderDto(x.getNumber(), bottle, x.getQuantity()));
            }
        });

        return orders;
    }

    private List<CrateOrderDto> getCratesWithQuantityFromOrder(Order order) {
        List<CrateOrderDto> orders = new ArrayList<>();

        order.getPositions().forEach(x -> {
            if(x.getBeverage() instanceof Crate) {
                Crate crate = (Crate) x.getBeverage();

                orders.add(new CrateOrderDto(x.getNumber(), crate, x.getQuantity()));
            }
        });

        return orders;
    }

    // we pass bottles and crate from the DB, such that we get the prices from the db
    // so that customer cannot cheat on the price
    public double calculatePrice(List<Bottle> bottlesFromDb, List<Crate> cratesFromDb) {
        double price = 0;

        if(bottles != null) {
            for (BottleOrderDto x : bottles) {
                double priceFromDb = bottlesFromDb.get(x.getBottleId()).getPrice();

                price += x.getQuantity() * priceFromDb;
            }
        }

        if(crates != null) {
            for (CrateOrderDto x : crates) {
                double priceFromDb = cratesFromDb.get(x.getCrateId()).getPrice();

                price += x.getQuantity() * priceFromDb;
            }
        }

        return price;
    }

    // we pass bottles and crates from the DB for correct IDs
    public List<OrderItem> getPositionsFromOrder(List<Bottle> bottlesFromDb, List<Crate> cratesFromDb) {
        List<OrderItem> positions = new ArrayList<>();

        int orderNumber = 10;

        if(getBottles() != null) {
            for (BottleOrderDto bottle : getBottles()) {
                OrderItem item = new OrderItem(
                        orderNumber,
                        bottlesFromDb.stream().filter(x -> x.getId() == bottle.getBottleId()).findFirst().orElse(null),
                        bottle.getQuantity());
                positions.add(item);

                orderNumber += 10;
            }
        }

        if(getCrates() != null) {
            for (CrateOrderDto crate : getCrates()) {
                OrderItem item = new OrderItem(
                        orderNumber,
                        cratesFromDb.stream().filter(x -> x.getId() == crate.getCrateId()).findFirst().orElse(null),
                        crate.getQuantity());
                positions.add(item);

                orderNumber += 10;
            }
        }

        return positions;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", bottles=" + bottles +
                ", crates=" + crates +
                ", price=" + price +
                ", status=" + status +
                ", href=" + href +
                '}';
    }
}
