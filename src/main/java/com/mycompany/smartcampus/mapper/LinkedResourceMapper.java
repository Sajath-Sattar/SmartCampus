package com.mycompany.smartcampus.mapper;

import com.mycompany.smartcampus.exception.LinkedResourceNotFoundException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
public class LinkedResourceMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    public Response toResponse(LinkedResourceNotFoundException ex) {
        return Response.status(422).entity("{\"error\":\"Room not found\"}").build();
    }
}