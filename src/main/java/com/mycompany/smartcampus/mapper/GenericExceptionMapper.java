package com.mycompany.smartcampus.mapper;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    public Response toResponse(Throwable ex) {
        return Response.status(500).entity("{\"error\":\"Internal Server Error\"}").build();
    }
}