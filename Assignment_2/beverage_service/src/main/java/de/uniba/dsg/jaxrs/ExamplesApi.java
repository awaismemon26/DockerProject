package de.uniba.dsg.jaxrs;

import de.uniba.dsg.jaxrs.controller.CustomerController;
import de.uniba.dsg.jaxrs.resources.SwaggerUI;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@ApplicationPath("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExamplesApi extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<>();

        resources.add(CustomerController.class);
        resources.add(SwaggerUI.class);

        return resources;
    }
}
