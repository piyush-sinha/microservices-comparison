package com.flipkart.affordability.external;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by piyushsinha.c on 29/08/17.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ExternalResource {
    @Path("external")
    @GET
    public Response sayHello() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.ok("Hello from External World!").build();
    }
}
