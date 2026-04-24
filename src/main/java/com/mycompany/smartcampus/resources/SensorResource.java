package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.DataStore;
import com.mycompany.smartcampus.model.*;
import com.mycompany.smartcampus.exception.LinkedResourceNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Collection<Sensor> getSensors(@QueryParam("type") String type) {
        if (type == null) return DataStore.sensors.values();

        List<Sensor> filtered = new ArrayList<>();
        for (Sensor s : DataStore.sensors.values()) {
            if (s.getType().equalsIgnoreCase(type)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    @POST
    public Response createSensor(Sensor sensor) {

        if (!DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException();
        }

        DataStore.sensors.put(sensor.getId(), sensor);
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @Path("/{id}/readings")
    public SensorReadingResource getReadingResource(@PathParam("id") String id) {
        return new SensorReadingResource(id);
    }
    @DELETE
    @Path("/{id}")
    public Response deleteSensor(@PathParam("id") String id) {

        Sensor sensor = DataStore.sensors.get(id);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found\"}")
                    .build();
        }

        // Remove sensor from room
        String roomId = sensor.getRoomId();
        if (roomId != null && DataStore.rooms.containsKey(roomId)) {
            DataStore.rooms.get(roomId).getSensorIds().remove(id);
        }

        // Remove sensor
        DataStore.sensors.remove(id);

        // Remove readings
        DataStore.readings.remove(id);

        return Response.noContent().build();
    }
}