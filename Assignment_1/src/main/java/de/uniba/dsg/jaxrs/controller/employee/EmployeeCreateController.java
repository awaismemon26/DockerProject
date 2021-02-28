package de.uniba.dsg.jaxrs.controller.employee;

import de.uniba.dsg.jaxrs.db.DB;
import de.uniba.dsg.jaxrs.dto.BottleDto;
import de.uniba.dsg.jaxrs.dto.CrateDto;
import de.uniba.dsg.jaxrs.dto.OrderDto;
import de.uniba.dsg.jaxrs.exceptions.OutOfStockException;
import de.uniba.dsg.jaxrs.model.Bottle;
import de.uniba.dsg.jaxrs.model.Crate;
import de.uniba.dsg.jaxrs.model.Order;
import de.uniba.dsg.jaxrs.model.OrderStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Path("employee/creates")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmployeeCreateController {

    private static final Logger logger = Logger.getLogger(String.valueOf(EmployeeCreateController.class));
    private static final DB database = DB.getInstance();

    @POST
    @Path("createBottle")
    public Response createBottle(@Context final UriInfo info, final BottleDto BottleDto) {
        logger.info("Employee created the following bottle " + BottleDto.toString());

        try {
            BottleDto response = database.createBottle(BottleDto, info);
            return Response.ok(response).build();
        } catch (URISyntaxException e) {
            return Response.status(
                    INTERNAL_SERVER_ERROR.getStatusCode(),
                    "could not construct URI").build();
        }
    }

    @POST
    @Path("createCrate")
    public Response createCrate(@Context final UriInfo info, final CrateDto CrateDto) {
        logger.info("Employee created the following crate " + CrateDto.toString());

        try {
            CrateDto response = database.createCrate(CrateDto, info);
            return Response.ok(response).build();
        } catch (URISyntaxException e) {
            return Response.status(
                    INTERNAL_SERVER_ERROR.getStatusCode(),
                    "could not construct URI").build();
        }
    }

    // for editing the bottle
    @PATCH
    @Path("editBottle/{bottleId}")
    public Response editBottle(@Context final UriInfo info,
                              @PathParam("bottleId") final int bottleId,
                              final BottleDto updatedBottle) {
        logger.info("Update request received for Bottle Id : " + bottleId);

        Optional<Bottle> bottleFromDb = database
                .getBottles()
                .stream()
                .filter(x -> x.getId() == bottleId)
                .findFirst();

        if(bottleFromDb == null)
        {
            return Response.noContent().build();
        }

        try {
            BottleDto response = database.editBottle(updatedBottle, bottleFromDb.get(), info);
            logger.info("Employee updated bottle with Id : " + bottleId);
            return Response.ok(response).build();
        } catch (URISyntaxException e) {
            return Response.status(
                    INTERNAL_SERVER_ERROR.getStatusCode(),
                    "could not construct URI").build();
        }
    }

    // for editing the crate
    @PATCH
    @Path("editCrate/{crateId}")
    public Response editCrate(@Context final UriInfo info,
                               @PathParam("crateId") final int crateId,
                               final CrateDto updatedCrate) {
        logger.info("Update request received for Crate Id : " + crateId);

        Optional<Crate> crateFromDb = database
                .getCrates()
                .stream()
                .filter(x -> x.getId() == crateId)
                .findFirst();

        if(crateFromDb == null)
        {
            return Response.noContent().build();
        }

        try {
            CrateDto response = database.editCrate(updatedCrate, crateFromDb.get(), info);
            logger.info("Employee updated crate with Id : " + crateId);
            return Response.ok(response).build();
        } catch (URISyntaxException e) {
            return Response.status(
                    INTERNAL_SERVER_ERROR.getStatusCode(),
                    "could not construct URI").build();
        }
    }


    // for processing the order
    @PATCH
    @Path("processOrder/{orderId}")
    public Response processOrder(@Context final UriInfo info,
                              @PathParam("orderId") final int orderId,
                              final OrderDto updatedOrder) {
        logger.info("Process request received for Order Id : " + orderId);

        Optional<Order> orderFromDb = database
                .getOrders()
                .stream()
                .filter(x -> x.getOrderId() == orderId)
                .findFirst();

        if (!orderFromDb.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "order with ID not found").build();
        }

        if (orderFromDb.get().getStatus() == OrderStatus.PROCESSED) {
            logger.info("Cannot process. Already Processed Order : " + orderId);
            return Response.status(NOT_ACCEPTABLE.getStatusCode()).build();
        }

        try {
            OrderDto response = database.processOrder(updatedOrder, orderFromDb.get(), info);
            logger.info("Employee processed order with Id : " + orderId);
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


}
