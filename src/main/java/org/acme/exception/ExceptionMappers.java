package org.acme.exception;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acme.utils.HttpResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {
    
    @ServerExceptionMapper
    @Produces(MediaType.APPLICATION_JSON)
    public Response map(Throwable e) {
        return Response
            .status(Status.INTERNAL_SERVER_ERROR)
            .entity(new HttpResponse("Error: "+e.getMessage()))
            .build();
    }

    @ServerExceptionMapper
    @Produces(MediaType.APPLICATION_JSON)
    public Response map(NotFound e) {
        return Response
            .status(Status.NOT_FOUND)
            .entity(new HttpResponse(e.getMessage()))
            .build();
    }

    @ServerExceptionMapper
    @Produces(MediaType.APPLICATION_JSON)
    public Response map(BadRequest e) {
        return Response
            .status(Status.BAD_REQUEST)
            .entity(new HttpResponse(e.getMessage()))
            .build();
    }

}
