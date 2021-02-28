package de.uniba.dsg.jaxrs.controller;

import com.google.gson.Gson;
import de.uniba.dsg.jaxrs.dto.*;
import de.uniba.dsg.jaxrs.middleware.DatabaseMiddleware;
import de.uniba.dsg.jaxrs.utils.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("employee")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmployeeController {

    private static final Logger logger = Logger.getLogger(EmployeeController.class.getSimpleName());
    private static final DatabaseMiddleware database = new DatabaseMiddleware();

    // region Create
    @POST
    @Path("bottles")
    public Response createBottle(@Context final UriInfo info, final BottleDto bottleDto) {
        logger.info("Employee created the following bottle " + bottleDto.toString());

        Either<DatabaseError, String> bottleEither = database.createBottle(bottleDto);

        if(bottleEither.isLeft()) {
            return Response.status(bottleEither.getLeft().getStatusCode(), bottleEither.getLeft().getStatusText()).build();
        }

        String bottlesAsJson = bottleEither.getRight();
        Gson gson = new Gson();

        BottleDto bottle = gson.fromJson(bottlesAsJson, BottleDto.class);
        bottle.setHref(info.getAbsolutePath().toString());

        return Response.ok(bottle).build();
    }

    @POST
    @Path("crates")
    public Response createCrate(@Context final UriInfo info, final CrateDto crateDto) {
        logger.info("Employee created the following crate " + crateDto.toString());

        Either<DatabaseError, String> crateEither = database.createCrate(crateDto);

        if(crateEither.isLeft()) {
            return Response.status(crateEither.getLeft().getStatusCode(), crateEither.getLeft().getStatusText()).build();
        }

        String crateAsJson = crateEither.getRight();
        Gson gson = new Gson();

        CrateDto crate = gson.fromJson(crateAsJson, CrateDto.class);
        crate.setHref(info.getAbsolutePath().toString());
        crate.getBottle().setHref(info.getBaseUri() + "employee/bottles/" + crate.getBottle().getId());

        return Response.ok(crate).build();

    }
    // endregion Create

    //region Read
    @GET
    @Path("bottles")
    public Response allBottles(@Context final UriInfo info) {
        logger.info("Employee requested to see all bottles");

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
        logger.info("Employee requested to see bottle with id " + bottleId);

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
        logger.info("Employee requested to see all crates");

        Either<DatabaseError, String> cratesEither = database.getCrates();

        if(cratesEither.isLeft()) {
            return Response.status(cratesEither.getLeft().getStatusCode(), cratesEither.getLeft().getStatusText()).build();
        }

        String cratesAsJson = cratesEither.getRight();
        Gson gson = new Gson();

        CrateDto[] crates = gson.fromJson(cratesAsJson, CrateDto[].class);

        for (CrateDto crate : crates) {
            crate.setHref(info.getAbsolutePath().toString() + "/" + crate.getId());
            crate.getBottle().setHref(info.getBaseUri() + "employee/bottles/" + crate.getBottle().getId());
        }

        return Response.ok(crates).build();
    }

    @GET
    @Path("crates/{crateId}")
    public Response getCrateWithId(@Context final UriInfo info, @PathParam("crateId") final int crateId) {
        logger.info("Employee requested to see crate with id " + crateId);

        Either<DatabaseError, String> crateEither = database.getCrateWithId(crateId);

        if(crateEither.isLeft()) {
            return Response.status(crateEither.getLeft().getStatusCode(), crateEither.getLeft().getStatusText()).build();
        }

        String crateAsJson = crateEither.getRight();
        Gson gson = new Gson();

        CrateDto crate = gson.fromJson(crateAsJson, CrateDto.class);
        crate.setHref(info.getAbsolutePath().toString());
        crate.getBottle().setHref(info.getBaseUri() + "employee/bottles/" + crate.getBottle().getId());

        return Response.ok(crate).build();
    }
    //endregion Read

    // region Update
    @PATCH
    @Path("bottles/{bottleId}")
    public Response editBottle(@Context final UriInfo info,
                               @PathParam("bottleId") final int bottleId,
                               final BottleDto updatedBottle) {
        logger.info("Employee requested to update bottle with id " + bottleId + "to following:\n" + updatedBottle.toString());

        Either<DatabaseError, String> bottleEither = database.editBottle(bottleId, updatedBottle);

        if(bottleEither.isLeft()) {
            return Response.status(bottleEither.getLeft().getStatusCode(), bottleEither.getLeft().getStatusText()).build();
        }

        String bottlesAsJson = bottleEither.getRight();
        Gson gson = new Gson();

        BottleDto bottle = gson.fromJson(bottlesAsJson, BottleDto.class);
        bottle.setHref(info.getAbsolutePath().toString());

        return Response.ok(bottle).build();
    }

    @PATCH
    @Path("crates/{crateId}")
    public Response editCrate(@Context final UriInfo info,
                              @PathParam("crateId") final int crateId,
                              final CrateDto updatedCrate) {
        logger.info("Employee requested to update crate with id " + crateId + "to following:\n" + updatedCrate.toString());

        Either<DatabaseError, String> crateEither = database.editCrate(crateId, updatedCrate);

        if(crateEither.isLeft()) {
            return Response.status(crateEither.getLeft().getStatusCode(), crateEither.getLeft().getStatusText()).build();
        }

        String crateAsJson = crateEither.getRight();
        Gson gson = new Gson();

        CrateDto crate = gson.fromJson(crateAsJson, CrateDto.class);
        crate.setHref(info.getAbsolutePath().toString());
        crate.getBottle().setHref(info.getBaseUri() + "employee/bottles/" + crate.getBottle().getId());

        return Response.ok(crate).build();
    }
    // endregion Update

    //region Delete
    @DELETE
    @Path("bottles/{bottleId}")
    public Response deleteBottleById(@Context final UriInfo info, @PathParam("bottleId") final int bottleId) {
        logger.info("Employee requested to delete bottle with id " + bottleId);

        Either<DatabaseError, String> bottleEither = database.deleteBottleWithId(bottleId);

        if(bottleEither.isLeft()) {
            return Response.status(bottleEither.getLeft().getStatusCode(), bottleEither.getLeft().getStatusText()).build();
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("crates/{crateId}")
    public Response deleteCrateById(@Context final UriInfo info, @PathParam("crateId") final int crateId) {
        logger.info("Employee requested to delete crate with id " + crateId);

        Either<DatabaseError, String> crateEither = database.deleteCrateWithId(crateId);

        if(crateEither.isLeft()) {
            return Response.status(crateEither.getLeft().getStatusCode(), crateEither.getLeft().getStatusText()).build();
        }

        return Response.noContent().build();
    }
    //endregion Delete
}
