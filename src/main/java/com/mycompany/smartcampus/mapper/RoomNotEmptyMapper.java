package com.mycompany.smartcampus.mapper;

import com.mycompany.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {
    public Response toResponse(RoomNotEmptyException ex) {
        return Response.status(409).entity("{\"error\":\"Room has sensors\"}").build();
    }
}