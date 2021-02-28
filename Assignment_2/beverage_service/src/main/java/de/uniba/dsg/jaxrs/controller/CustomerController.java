package de.uniba.dsg.jaxrs.controller;

import com.google.gson.Gson;
import de.uniba.dsg.jaxrs.dto.*;
import de.uniba.dsg.jaxrs.middleware.DatabaseMiddleware;
import de.uniba.dsg.jaxrs.utils.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("customer")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CustomerController {

    private static final Logger logger = Logger.getLogger(CustomerController.class.getSimpleName());
    private static final DatabaseMiddleware database = new DatabaseMiddleware();

    @GET
    @Path("bottles")
    public Response allBottles(@Context final UriInfo info) {
        logger.info("User requested to see all bottles");
        Either<DatabaseError, String> bottlesEither = database.getBottles();

        if(bottlesEither.isLeft()) {
            return Response.status(bottlesEither.getLeft().getStatusCode(), bottlesEither.getLeft().getStatusText()).build();
        }

        String bottlesAsJson = bottlesEither.getRight();
        Gson gson = new Gson();

        BottleDto[] bottles = gson.fromJson(bottlesAsJson, BottleDto[].class);

        for (BottleDto bottle : bottles) {
            bottle.setHref(info.getAbsolutePath().toString() + "/" + bottle.getId());
        }

        return Response.ok(bottles).build();
    }

    @GET
    @Path("bottles/{bottleId}")
    public Response getBottleWithId(@Context final UriInfo info, @PathParam("bottleId") final int bottleId) {
        logger.info("User requested to see bottle with id " + bottleId);

        Either<DatabaseError, String> bottleEither = database.getBottleWithId(bottleId);

        if(bottleEither.isLeft()) {
            return Response.status(bottleEither.getLeft().getStatusCode(), bottleEither.getLeft().getStatusText()).build();
        }

        String bottlesAsJson = bottleEither.getRight();
        Gson gson = new Gson();

        BottleDto bottle = gson.fromJson(bottlesAsJson, BottleDto.class);
        bottle.setHref(info.getAbsolutePath().toString());

        return Response.ok(bottle).build();
    }

    @GET
    @Path("crates")
    public Response allCrates(@Context final UriInfo info) {
        logger.info("User requested to see all crates");
        Either<DatabaseError, String> cratesEither = database.getCrates();

        if(cratesEither.isLeft()) {
            return Response.status(cratesEither.getLeft().getStatusCode(), cratesEither.getLeft().getStatusText()).build();
        }

        String cratesAsJson = cratesEither.getRight();
        Gson gson = new Gson();

        CrateDto[] crates = gson.fromJson(cratesAsJson, CrateDto[].class);

        for (CrateDto crate : crates) {
            crate.setHref(info.getAbsolutePath().toString() + "/" + crate.getId());
            crate.getBottle().setHref(info.getBaseUri() + "customer/bottles/" + crate.getBottle().getId());
        }

        return Response.ok(crates).build();
    }

    @GET
    @Path("crates/{crateId}")
    public Response getCrateWithId(@Context final UriInfo info, @PathParam("crateId") final int crateId) {
        logger.info("User requested to see crate with id " + crateId);
        Either<DatabaseError, String> crateEither = database.getCrateWithId(crateId);

        if(crateEither.isLeft()) {
            return Response.status(crateEither.getLeft().getStatusCode(), crateEither.getLeft().getStatusText()).build();
        }

        String crateAsJson = crateEither.getRight();
        Gson gson = new Gson();

        CrateDto crate = gson.fromJson(crateAsJson, CrateDto.class);
        crate.setHref(info.getAbsolutePath().toString());
        crate.getBottle().setHref(info.getBaseUri() + "customer/bottles/" + crate.getBottle().getId());


        return Response.ok(crate).build();
    }
}
