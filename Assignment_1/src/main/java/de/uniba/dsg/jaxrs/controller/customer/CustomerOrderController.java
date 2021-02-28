package de.uniba.dsg.jaxrs.controller.customer;

import de.uniba.dsg.jaxrs.db.DB;
import de.uniba.dsg.jaxrs.dto.OrderDto;
import de.uniba.dsg.jaxrs.exceptions.OutOfStockException;
import de.uniba.dsg.jaxrs.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Path("customer/orders")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CustomerOrderController {

    private static final Logger logger = Logger.getLogger(CustomerBottleController.class.getSimpleName());
    private static final DB database = DB.getInstance();

    @GET
    @Path("{orderId}")
    public Response getOrder(@Context final UriInfo info, @PathParam("orderId") final int orderId) {
        logger.info("Customer viewed details of order with id " + orderId);

        Optional<Order> order = database.getOrders().stream().filter(x -> x.getOrderId() == orderId).findFirst();

        if (order.isPresent()) {
            OrderDto orderDto = new OrderDto(order.get(), info.getAbsolutePath());
            return Response.ok(orderDto).build();
        } else {
            return Response.status(NOT_FOUND.getStatusCode(), "order with ID not found").build();
        }
    }

    @POST
    @Path("placeOrder")
    public Response placeOrder(@Context final UriInfo info, final OrderDto orderDto) {
        logger.info("Customer placed following order " + orderDto.toString());

        try {
            OrderDto response = database.placeOrder(orderDto, info);
            return Response.ok(response).build();
        } catch (OutOfStockException e) {
            return Response.status(
                    EXPECTATION_FAILED.getStatusCode(),
                    "Not enough supplies in stock")
                    .build();
        } catch (URISyntaxException e) {
            return Response.status(
                    INTERNAL_SERVER_ERROR.getStatusCode(),
                    "could not construct URI").build();
        }
    }

    @PATCH
    @Path("editOrder/{orderId}")
    public Response editOrder(@Context final UriInfo info,
                              @PathParam("orderId") final int orderId,
                              final OrderDto updatedOrder) {
        logger.info("Customer is editing order with id " + orderId);

        Optional<Order> orderFromDb = database
                .getOrders()
                .stream()
                .filter(x -> x.getOrderId() == orderId)
                .findFirst();

        if (!orderFromDb.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "order with ID not found").build();
        }

        if (orderFromDb.get().getStatus() == OrderStatus.PROCESSED) {
            return Response.status(NOT_ACCEPTABLE.getStatusCode(), "cannot edit an already processed order").build();
        }

        try {
            OrderDto response = database.editOrder(updatedOrder, orderFromDb.get(), info);
            return Response.ok(response).build();
        } catch (OutOfStockException e) {
            return Response.status(
                    EXPECTATION_FAILED.getStatusCode(),
                    "Not enough supplies in stock")
                    .build();
        } catch (URISyntaxException e) {
            return Response.status(
                    INTERNAL_SERVER_ERROR.getStatusCode(),
                    "could not construct URI").build();
        }
    }

    @DELETE
    @Path("deleteOrder/{orderId}")
    public Response deleteOrder(@Context final UriInfo info, @PathParam("orderId") final int orderId) {
        logger.info("Customer requested to delete order with id " + orderId);

        Optional<Order> orderFromDb = database
                .getOrders()
                .stream()
                .filter(x -> x.getOrderId() == orderId)
                .findFirst();

        if(!orderFromDb.isPresent()){
            return Response.status(NOT_FOUND.getStatusCode(), "order with ID not found").build();
        }

        if (orderFromDb.get().getStatus() == OrderStatus.PROCESSED) {
            return Response.status(NOT_ACCEPTABLE.getStatusCode(), "cannot cancel an already processed order").build();
        }

        database.cancelOrder(orderId);
        return Response.noContent().build();
    }
}
