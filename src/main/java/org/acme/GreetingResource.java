package org.acme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/sistema")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("datahora")
    public String hello() {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY - HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String ping() {
        return "pong: " + UUID.randomUUID().toString();
    }
}