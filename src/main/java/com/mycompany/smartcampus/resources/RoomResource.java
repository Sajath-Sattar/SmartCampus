package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.DataStore;
import com.mycompany.smartcampus.model.Room;
import com.mycompany.smartcampus.exception.RoomNotEmptyException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public Collection<Room> getRooms() {
        return DataStore.rooms.values();
    }

    @POST
    public Response createRoom(Room room) {
        DataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{id}")
    public Room getRoom(@PathParam("id") String id) {
        return DataStore.rooms.get(id);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = DataStore.rooms.get(id);

        if (room.getSensorIds().size() > 0) {
            throw new RoomNotEmptyException();
        }

        DataStore.rooms.remove(id);
        return Response.noContent().build();
    }
}