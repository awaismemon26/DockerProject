package de.uniba.dsg.jaxrs.controller.customer;

import de.uniba.dsg.jaxrs.db.DB;
import de.uniba.dsg.jaxrs.dto.BottleDto;
import de.uniba.dsg.jaxrs.dto.CrateDto;
import de.uniba.dsg.jaxrs.model.Bottle;
import de.uniba.dsg.jaxrs.model.Crate;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.uniba.dsg.jaxrs.dto.BottleDto.getListForCustomerWithUri;
import static de.uniba.dsg.jaxrs.dto.CrateDto.getCListForCustomerWithUri;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("customer/bottles")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CustomerBottleController {

    private static final Logger logger = Logger.getLogger(CustomerBottleController.class.getSimpleName());
    private static final DB database = DB.getInstance();

    @GET
    public Response viewBeverages(@Context final UriInfo info) {
        logger.info("User requested to see all beverages");
        List<Bottle> bottles = database.getBottles();

        GenericEntity<List<BottleDto>> response = new GenericEntity<List<BottleDto>>(getListForCustomerWithUri(info, bottles)) {};
        return Response.ok(response).build();
    }

    // for viewing crates
    @GET
    @Path("crates")
    public Response viewBeverageCrates(@Context final UriInfo info) {
        logger.info("User requested to see all crates.");
        List<Crate> crates = database.getCrates();

        GenericEntity<List<CrateDto>> response = new GenericEntity<List<CrateDto>>(getCListForCustomerWithUri(info, crates)) {};
        return Response.ok(response).build();
    }

    @GET
    @Path("{bottleId}")
    public Response getBottleById(@Context final UriInfo info, @PathParam("bottleId") final int bottleId) {
        logger.info("Get bottle with id " + bottleId);

        Optional<Bottle> bottle = database.
                getBottles()
                .stream()
                .filter(x -> x.getId() == bottleId)
                .findFirst();

        if(bottle.isPresent()) {
            BottleDto response = new BottleDto(bottle.get(), info.getAbsolutePath());
            return Response.ok(response).build();
        } else {
            return Response.status(NOT_FOUND).build();
        }
    }

    @GET
    @Path("searchByName")
    public Response searchBottleByName(@Context final UriInfo info, @QueryParam("name") final String nameFilter) {
        logger.info("Searching for bottles with name " + nameFilter);

        List<Bottle> bottles = database
                .getBottles()
                .stream()
                .filter(x -> x.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .collect(Collectors.toList());

        GenericEntity<List<BottleDto>> response = new GenericEntity<List<BottleDto>>(getListForCustomerWithUri(info, bottles)) {};
        return Response.ok(response).build();
    }

    @GET
    @Path("searchByPrice")
    public Response searchBottleByPrice(@Context final UriInfo info,
                                           @QueryParam("minPrice") final String minPrice,
                                           @QueryParam("maxPrice") final String maxPrice) {
        logger.info("Searching for bottles within price range: " + minPrice + " - " + maxPrice);

        try {
            double minePriceD = Double.parseDouble(minPrice);
            double maxPriceD = Double.parseDouble(maxPrice);

            List<Bottle> bottles = database
                    .getBottles()
                    .stream()
                    .filter(x -> x.getPrice() > minePriceD && x.getPrice() < maxPriceD)
                    .collect(Collectors.toList());

            GenericEntity<List<BottleDto>> response = new GenericEntity<List<BottleDto>>(getListForCustomerWithUri(info, bottles)) {};
            return Response.ok(response).build();
        } catch (Exception ignored) {
            int statusCode = Response.Status.BAD_REQUEST.getStatusCode();
            String responseText = "Price range must be valid double value, like '2' or '3.5'";
            return Response.status(statusCode, responseText).build();
        }
    }
}
