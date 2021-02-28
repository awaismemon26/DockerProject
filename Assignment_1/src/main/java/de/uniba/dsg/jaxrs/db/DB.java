package de.uniba.dsg.jaxrs.db;

import de.uniba.dsg.jaxrs.dto.*;
import de.uniba.dsg.jaxrs.model.*;
import de.uniba.dsg.jaxrs.exceptions.OutOfStockException;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static de.uniba.dsg.jaxrs.model.OrderStatus.*;

public class DB {

    public static DB instance;

    private final List<Bottle> bottles;
    private final List<Crate> crates;
    private final List<Order> orders;

    private DB() {
        this.bottles = initBottles();
        this.crates = initCrates(); // updated initCases to initCrates because it was a bit confusing
        this.orders = initOrder();
    }

    public static synchronized DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }

        return instance;
    }

    public List<Bottle> getBottles() {
        return bottles;
    }

    public List<Crate> getCrates() {
        return crates;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public synchronized OrderDto placeOrder(OrderDto orderDto, UriInfo info) throws OutOfStockException, URISyntaxException {
        saveDbStateAndReduceFromStockOrResetStateAndThrowException(orderDto);

        int orderId = getOrders()
                .stream()
                .max(Comparator.comparing(Order::getOrderId))
                .map(x -> x.getOrderId() + 1)
                .orElse(0);
        List<OrderItem> positions = orderDto.getPositionsFromOrder(bottles, crates);
        double price = orderDto.calculatePrice(bottles, crates);

        Order newOrder = new Order(orderId, positions, price, SUBMITTED);

        orders.add(newOrder);

        return new OrderDto(newOrder, new URI(info.getBaseUri() + "customers/orders/" + orderId));
    }

    //region Customer methods

    public synchronized OrderDto editOrder(OrderDto orderDto, Order orderFromDb, UriInfo info)
            throws OutOfStockException, URISyntaxException {
        undoOrder(orderDto);
        saveDbStateAndReduceFromStockOrResetStateAndThrowException(orderDto);

        if (orderDto.getBottles() == null) orderDto.setBottles(new ArrayList<>());
        if (orderDto.getCrates() == null) orderDto.setCrates(new ArrayList<>());
        orderDto.setId(orderFromDb.getOrderId());
        orderDto.setPrice(orderDto.calculatePrice(bottles, crates));
        orderDto.setStatus(orderFromDb.getStatus());
        orderDto.setHref(new URI(info.getBaseUri() + "customer/orders/" + orderFromDb.getOrderId()));

        Order order = orders.stream().filter(x -> x.getOrderId() == orderFromDb.getOrderId()).findFirst().get();
        order.setPositions(orderDto.getPositionsFromOrder(bottles, crates));

        return orderDto;
    }

    public synchronized void cancelOrder(int orderId) {
        Optional<Order> orderFromDb = orders
                .stream()
                .filter(x -> x.getOrderId() == orderId)
                .findFirst();

        if (orderFromDb.isPresent()) {
            orders.remove(orderFromDb.get());
            undoOrder(new OrderDto(orderFromDb.get(), null));
        }
    }

    private synchronized void undoOrder(OrderDto orderToUndo) {
        if (orderToUndo.getBottles() != null) {
            for (BottleOrderDto position : orderToUndo.getBottles()) {
                Bottle bottleFromDb = bottles
                        .stream()
                        .filter(x -> x.getId() == position.getBottleId())
                        .findFirst()
                        .orElse(null);

                if (bottleFromDb == null) continue;

                bottleFromDb.setInStock(bottleFromDb.getInStock() + position.getQuantity());
            }
        }

        if (orderToUndo.getCrates() != null) {
            for (CrateOrderDto position : orderToUndo.getCrates()) {
                Crate crateFromDb = crates
                        .stream()
                        .filter(x -> x.getId() == position.getCrateId())
                        .findFirst()
                        .orElse(null);

                if (crateFromDb == null) continue;

                crateFromDb.setInStock(crateFromDb.getInStock() + position.getQuantity());
            }
        }
    }
    //endregion


    // region Employee Methods

    // create Bottles
    public synchronized BottleDto createBottle(BottleDto bottleDto, UriInfo info) throws URISyntaxException {
        int bottleId = getBottles()
                .stream()
                .max(Comparator.comparing(Bottle::getId))
                .map(x -> x.getId() + 1)
                .orElse(0);
        String name = bottleDto.getName();
        double volume = bottleDto.getVolume();
        boolean isAlcoholic = bottleDto.isAlcoholic();
        double volumePercent = bottleDto.getVolumePercent();
        double price = bottleDto.getPrice();
        String supplier = bottleDto.getSupplier();
        int inStock = bottleDto.getInStock();

        Bottle newBottle = new Bottle(bottleId, name, volume, isAlcoholic, volumePercent, price, supplier, inStock);

        bottles.add(newBottle);

        return new BottleDto(newBottle, new URI(info.getBaseUri() + "employee/addedBottle/" + bottleId));
    }

    // create Crates
    public synchronized CrateDto createCrate(CrateDto crateDto, UriInfo info) throws URISyntaxException {

        int crateId = getCrates()
                .stream()
                .max(Comparator.comparing(Crate::getId))
                .map(x -> x.getId() + 1)
                .orElse(0);
        Bottle bottle = crateDto.getBottle();
        int noOfBottles = crateDto.getNoOfBottles();
        double price = crateDto.getPrice();
        int inStock = crateDto.getInStock();

        Crate newCrate = new Crate(crateId, bottle, noOfBottles, price, inStock);

        crates.add(newCrate);

        return new CrateDto(newCrate, new URI(info.getBaseUri() + "employee/addedCrate/" + crateId));
    }

    // update Bottles
    public synchronized BottleDto editBottle(BottleDto bottleDto, Bottle bottleFromDb, UriInfo info) throws URISyntaxException {

        bottleDto.setId(bottleFromDb.getId());
        bottleDto.setHref(new URI(info.getBaseUri() + "employee/updatedBottle/" + bottleFromDb.getId()));

        Bottle bottle = bottles.stream().filter(x -> x.getId() == bottleFromDb.getId()).findFirst().get();
        bottle.setName(bottleDto.getName());
        bottle.setVolume(bottleDto.getVolume());
        bottle.setAlcoholic(bottleDto.isAlcoholic());
        bottle.setVolumePercent(bottleDto.getVolumePercent());
        bottle.setPrice(bottleDto.getPrice());
        bottle.setSupplier(bottleDto.getSupplier());
        bottle.setInStock(bottleDto.getInStock());
        return bottleDto;
    }

    // update Crates
    public synchronized CrateDto editCrate(CrateDto crateDto, Crate crateFromDb, UriInfo info) throws URISyntaxException {

        crateDto.setId(crateFromDb.getId());
        crateDto.setHref(new URI(info.getBaseUri() + "employee/updatedCrate/" + crateFromDb.getId()));

        Crate crate = crates.stream().filter(x -> x.getId() == crateFromDb.getId()).findFirst().get();
        crate.setBottle(crateDto.getBottle());
        crate.setNoOfBottles(crateDto.getNoOfBottles());
        crate.setPrice(crateDto.getPrice());
        crate.setInStock(crateDto.getInStock());
        return crateDto;
    }


    // update order status from submitted to processed
    public synchronized OrderDto processOrder(OrderDto orderDto, Order orderFromDb, UriInfo info)
            throws OutOfStockException, URISyntaxException {
        undoOrder(orderDto);
        saveDbStateAndReduceFromStockOrResetStateAndThrowException(orderDto);

        if (orderDto.getBottles() == null) orderDto.setBottles(new ArrayList<>());
        if (orderDto.getCrates() == null) orderDto.setCrates(new ArrayList<>());
        orderDto.setId(orderFromDb.getOrderId());
        orderDto.setPrice(orderDto.calculatePrice(bottles, crates));
        orderDto.setStatus(PROCESSED);
        orderDto.setHref(new URI(info.getBaseUri() + "employee/processedOrder/" + orderFromDb.getOrderId()));

        Order order = orders.stream().filter(x -> x.getOrderId() == orderFromDb.getOrderId()).findFirst().get();
        order.setPositions(orderDto.getPositionsFromOrder(bottles, crates));
        order.setStatus(PROCESSED);

        return orderDto;
    }

    // end Employee Methods region


    //region helper methods
    private synchronized void saveDbStateAndReduceFromStockOrResetStateAndThrowException(OrderDto orderDto)
            throws OutOfStockException {
        List<Bottle> bottlesBeforeTransaction = new ArrayList<>(bottles);
        List<Crate> cratesBeforeTransaction = new ArrayList<>(crates);

        try {
            reduceFromStock(orderDto);
        } catch (Exception ignored) {
            this.bottles.clear();
            this.crates.clear();

            this.bottles.addAll(bottlesBeforeTransaction);
            this.crates.addAll(cratesBeforeTransaction);

            throw new OutOfStockException();
        }
    }

    private synchronized void reduceFromStock(OrderDto orderDto) throws OutOfStockException {
        if (orderDto.getBottles() != null) {
            for (BottleOrderDto bottle : orderDto.getBottles()) {
                int bottlesToSubtract = bottle.getQuantity();
                Optional<Bottle> bottleInSupply = bottles
                        .stream()
                        .filter(x -> x.getId() == bottle.getBottleId())
                        .findFirst();

                if (!bottleInSupply.isPresent()) throw new OutOfStockException();

                int newBottleCount = bottleInSupply.get().getInStock() - bottlesToSubtract;
                if (newBottleCount < 0) throw new OutOfStockException();

                bottleInSupply.get().setInStock(newBottleCount);
            }
        }

        if (orderDto.getCrates() != null) {
            for (CrateOrderDto crate : orderDto.getCrates()) {
                int cratesToSubtract = crate.getQuantity();
                Optional<Crate> crateInSupply = crates
                        .stream()
                        .filter(x -> x.getId() == crate.getCrateId())
                        .findFirst();

                if (!crateInSupply.isPresent()) throw new OutOfStockException();


                int newCrateCount = crateInSupply.get().getInStock() - cratesToSubtract;
                if (newCrateCount < 0) throw new OutOfStockException();

                crateInSupply.get().setInStock(newCrateCount);
            }
        }
    }
    //endregion

    //region database seeding
    private List<Bottle> initBottles() {
        return new ArrayList<>(Arrays.asList(
                new Bottle(1, "Pils", 0.5, true, 4.8, 0.79, "Keesmann", 34),
                new Bottle(2, "Helles", 0.5, true, 4.9, 0.89, "Mahr", 17),
                new Bottle(3, "Boxbeutel", 0.75, true, 12.5, 5.79, "Divino", 11),
                new Bottle(4, "Tequila", 0.7, true, 40.0, 13.79, "Tequila Inc.", 5),
                new Bottle(5, "Gin", 0.5, true, 42.00, 11.79, "Hopfengarten", 3),
                new Bottle(6, "Export Edel", 0.5, true, 4.8, 0.59, "Oettinger", 66),
                new Bottle(7, "Premium Tafelwasser", 0.7, false, 0.0, 4.29, "Franken Brunnen", 12),
                new Bottle(8, "Wasser", 0.5, false, 0.0, 0.29, "Franken Brunnen", 57),
                new Bottle(9, "Spezi", 0.7, false, 0.0, 0.69, "Franken Brunnen", 42),
                new Bottle(10, "Grape Mix", 0.5, false, 0.0, 0.59, "Franken Brunnen", 12),
                new Bottle(11, "Still", 1.0, false, 0.0, 0.66, "Franken Brunnen", 34),
                new Bottle(12, "Cola", 1.5, false, 0.0, 1.79, "CCC", 69),
                new Bottle(13, "Cola Zero", 2.0, false, 0.0, 2.19, "CCC", 12),
                new Bottle(14, "Apple", 0.5, false, 0.0, 1.99, "Juice Factory", 25),
                new Bottle(15, "Orange", 0.5, false, 0.0, 1.99, "Juice Factory", 55),
                new Bottle(16, "Lime", 0.5, false, 0.0, 2.99, "Juice Factory", 8)
        ));
    }

    private List<Crate> initCrates() {
        return new ArrayList<>(Arrays.asList(
                new Crate(1, this.bottles.get(0), 20, 14.99, 3),
                new Crate(2, this.bottles.get(1), 20, 15.99, 5),
                new Crate(3, this.bottles.get(2), 6, 30.00, 7),
                new Crate(4, this.bottles.get(7), 12, 1.99, 11),
                new Crate(5, this.bottles.get(8), 20, 11.99, 13),
                new Crate(6, this.bottles.get(11), 6, 10.99, 4),
                new Crate(7, this.bottles.get(12), 6, 11.99, 5),
                new Crate(8, this.bottles.get(13), 20, 35.00, 7),
                new Crate(9, this.bottles.get(14), 12, 20.00, 9)
        ));
    }

    private List<Order> initOrder() {
        return new ArrayList<>(Arrays.asList(
                new Order(1, new ArrayList<>(Arrays.asList(
                        new OrderItem(10, this.bottles.get(3), 2),
                        new OrderItem(20, this.crates.get(3), 1),
                        new OrderItem(30, this.bottles.get(15), 1)
                )), 32.56, SUBMITTED),
                new Order(2, new ArrayList<>(Arrays.asList(
                        new OrderItem(10, this.bottles.get(13), 2),
                        new OrderItem(20, this.bottles.get(14), 2),
                        new OrderItem(30, this.crates.get(0), 1)
                )), 22.95, PROCESSED),
                new Order(3, new ArrayList<>(Arrays.asList(
                        new OrderItem(10, this.bottles.get(2), 1)
                )), 5.79, SUBMITTED)
        ));
    }

    //endregion
}
