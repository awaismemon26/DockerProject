package de.uniba.dsg.jaxrs.middleware.controller;

import de.uniba.dsg.jaxrs.db.DB;
import de.uniba.dsg.jaxrs.dto.*;
import de.uniba.dsg.jaxrs.model.*;
import de.uniba.dsg.jaxrs.utils.conversion.CustomerConversion;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.uniba.dsg.jaxrs.utils.conversion.CustomerConversion.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("customer")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CustomerController {

    private static final Logger logger = Logger.getLogger(CustomerController.class.getSimpleName());
    private static final DB database = DB.getInstance();

    @GET
    @Path("bottles")
    public Response viewBeverages(@Context final UriInfo info) {
        logger.info("User requested to see all beverages");
        List<Bottle> bottles = database.getBottles();

        List<BottleDto> bottleDtos = bottles.stream().map(CustomerConversion::toDto).collect(Collectors.toList());

        GenericEntity<List<BottleDto>> response = new GenericEntity<List<BottleDto>>(bottleDtos) {};
        return Response.ok(response).build();
    }

    @GET
    @Path("bottles/{bottleId}")
    public Response getBottleById(@Context final UriInfo info, @PathParam("bottleId") final int bottleId) {
        logger.info("Get bottle with id " + bottleId);

        Optional<Bottle> bottle = database.getBottleById(bottleId);

        if(!bottle.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find bottle with id" + bottleId).build();
        }

        BottleDto bottleDto = toDto(bottle.get());

        return Response.ok(bottleDto).build();
    }

    @GET
    @Path("crates")
    public Response viewBeverageCrates(@Context final UriInfo info) {
        logger.info("User requested to see all crates.");
        List<Crate> crates = database.getCrates();

        List<CrateDto> crateDtos = crates.stream().map(CustomerConversion::toDto).collect(Collectors.toList());

        GenericEntity<List<CrateDto>> response = new GenericEntity<List<CrateDto>>(crateDtos) {};
        return Response.ok(response).build();
    }

    @GET
    @Path("crates/{crateId}")
    public Response getCrateById(@Context final UriInfo info, @PathParam("crateId") final int crateId) {
        logger.info("Get crate with id " + crateId);

        Optional<Crate> crate = database.getCrateById(crateId);

        if(!crate.isPresent()) {
            return Response.status(NOT_FOUND.getStatusCode(), "Could not find crate with id" + crateId).build();
        }

        CrateDto crateDto = toDto(crate.get());

        return Response.ok(crateDto).build();
    }
}
