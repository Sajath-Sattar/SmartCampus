package com.mycompany.smartcampus;

import com.mycompany.smartcampus.model.*;
import java.util.*;

public class DataStore {

    public static Map<String, Room> rooms = new HashMap<>();
    public static Map<String, Sensor> sensors = new HashMap<>();
    public static Map<String, List<SensorReading>> readings = new HashMap<>();

    static {

        // ---- Rooms ----
        Room r1 = new Room("LIB-301", "Library Study Room", 50);
        Room r2 = new Room("ENG-101", "Engineering Lab", 30);

        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        // ---- Sensors ----
        Sensor s1 = new Sensor();
        s1.setId("TEMP-001");
        s1.setType("Temperature");
        s1.setStatus("ACTIVE");
        s1.setCurrentValue(25.0);
        s1.setRoomId("LIB-301");

        Sensor s2 = new Sensor();
        s2.setId("CO2-001");
        s2.setType("CO2");
        s2.setStatus("MAINTENANCE");
        s2.setCurrentValue(400.0);
        s2.setRoomId("ENG-101");

        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);

        // Link sensors to rooms
        r1.getSensorIds().add(s1.getId());
        r2.getSensorIds().add(s2.getId());

        // ---- Readings ----
        List<SensorReading> tempReadings = new ArrayList<>();

        SensorReading sr1 = new SensorReading();
        sr1.setId("r1");
        sr1.setTimestamp(System.currentTimeMillis());
        sr1.setValue(25.0);

        tempReadings.add(sr1);

        readings.put("TEMP-001", tempReadings);
    }
}