package de.uniba.dsg.jaxrs.middleware.controller;

import de.uniba.dsg.jaxrs.db.DB;
import de.uniba.dsg.jaxrs.dto.*;
import de.uniba.dsg.jaxrs.model.*;
import de.uniba.dsg.jaxrs.utils.conversion.EmployeeConversion;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.uniba.dsg.jaxrs.utils.conversion.EmployeeConversion.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Path("employee")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmployeeController {

    private static final Logger logger = Logger.getLogger(String.valueOf(EmployeeController.class));
    private static final DB database = DB.getInstance();

    //region Create
    @POST
    @Path("bottles")
    public Response createBottle(@Context final UriInfo info, final BottleDto bottleDto) {
        logger.info("Employee created the following bottle " + bottleDto.toString());

        Bottle bottleToAdd = toModel(bottleDto);
        Bottle addedBottle = database.createBottle(bottleToAdd);

        BottleDto result = toDto(addedBottle);
        return Response.ok(result).status(CREATED).build();
    }

    @POST
    @Path("crates")
    public Response createCrate(@Context final UriInfo info, final CrateDto crateDto) {
        logger.info("Employee created the following crate " + crateDto.toString());

        Crate crateToAdd = toModel(crateDto);
        Crate addedCrate = database.createCrate(crateToAdd);

        CrateDto result = toDto(addedCrate);
        return Response.ok(result).status(CREATED).build();
    }
    //endregion

    //region Read
    @GET
    @Path("bottles")
    public Response getBottles(@Context final UriInfo info) {
        logger.info("Employee requested to see all bottles");

        List<Bottle> bottles = database.getBottles();

        List<BottleDto> bottleDtos = bottles.stream().map(EmployeeConversion::toDto).collect(Collectors.toList());

        GenericEntity<List<BottleDto>> response = new GenericEntity<List<BottleDto>>(bottleDtos) {};
        return Response.ok(response).build();
    }

    @GET
    @Path("bottles/{bottleId}")
    public Response getBottleById(@Context final UriInfo info, @PathParam("bottleId") final int bottleId) {
        logger.info("Employee requested to view bottle with id " + bottleId);

        Optional<Bottle> bottle = database.getBottleById(bottleId);

        if(!bottle.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find bottle with id" + bottleId).build();
        }

        BottleDto result = toDto(bottle.get());
        return Response.ok(result).build();
    }

    @GET
    @Path("crates")
    public Response getCrates(@Context final UriInfo info) {
        logger.info("Employee requested to see all crates");

        List<Crate> crates = database.getCrates();

        List<CrateDto> crateDtos = crates.stream().map(EmployeeConversion::toDto).collect(Collectors.toList());

        GenericEntity<List<CrateDto>> response = new GenericEntity<List<CrateDto>>(crateDtos) {};
        return Response.ok(response).build();
    }

    @GET
    @Path("crates/{crateId}")
    public Response getCrateById(@Context final UriInfo info, @PathParam("crateId") final int crateId) {
        logger.info("Employee requested to view crate with id " + crateId);

        Optional<Crate> crate = database.getCrateById(crateId);

        if(!crate.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find crate with id" + crateId).build();
        }

        CrateDto result = toDto(crate.get());
        return Response.ok(result).build();
    }
    //endregion

    //region Update
    @PATCH
    @Path("bottles/{bottleId}")
    public Response editBottle(@Context final UriInfo info,
                              @PathParam("bottleId") final int bottleId,
                              final BottleDto updatedBottle) {
        logger.info("Employee requested to update bottle with id " + bottleId + "to following:\n" + updatedBottle.toString());

        Bottle bottleToUpdate = toModel(updatedBottle);
        Optional<Bottle> update = database.updateBottleWithId(bottleId, bottleToUpdate);

        if(!update.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find bottle with id" + bottleId + ", so it couldn't be updated").build();
        }

        BottleDto result = toDto(update.get());
        return Response.ok(result).build();
    }

    @PATCH
    @Path("crates/{crateId}")
    public Response editCrate(@Context final UriInfo info,
                               @PathParam("crateId") final int crateId,
                               final CrateDto updatedCrate) {
        logger.info("Employee requested to update crate with id " + crateId + "to following:\n" + updatedCrate.toString());

        Crate crateToUpdate = toModel(updatedCrate);
        Optional<Crate> update = database.updateCrateWithId(crateId, crateToUpdate);

        if(!update.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find crate with id" + crateId + ", so it couldn't be updated").build();
        }

        CrateDto result = toDto(update.get());
        return Response.ok(result).build();
    }
    //endregion

    //region Delete
    @DELETE
    @Path("bottles/{bottleId}")
    public Response deleteBottleById(@Context final UriInfo info, @PathParam("bottleId") final int bottleId) {
        logger.info("Employee requested to delete bottle with id " + bottleId);

        boolean success = database.deleteBottleWithId(bottleId);

        if(success) {
            return Response.noContent().build();
        } else {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find bottle with id" + bottleId).build();
        }
    }

    @DELETE
    @Path("crates/{crateId}")
    public Response deleteCrateById(@Context final UriInfo info, @PathParam("crateId") final int crateId) {
        logger.info("Employee requested to delete crate with id " + crateId);

        boolean success = database.deleteCrateWithId(crateId);

        if(success) {
            return Response.noContent().build();
        } else {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find crate with id" + crateId).build();
        }
    }
    //endregion
}
