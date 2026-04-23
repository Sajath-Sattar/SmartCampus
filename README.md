# SmartCampus REST API  
**Author:** Sajath Sattar (W2153402)  

---

## Overview

SmartCampus is a RESTful API built using **JAX-RS (Jersey)** on **Java EE 8**, designed to manage university campus infrastructure including:

- Rooms
- Sensors
- Sensor Readings

The system follows REST principles with proper resource hierarchy, HTTP status codes, and error handling.

---

## Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Sajath-Sattar/SmartCampus.git
cd SmartCampus
````

---

### 2. Build Project

Make sure you have:

* Java 8
* Maven

Then run:

```bash
mvn clean install
```

---

### 3. Deploy to Tomcat

* Copy the generated `.war` file from:

```
target/SmartCampus.war
```

* Place it in:

```
Tomcat/webapps/
```

* Start Tomcat

---

### 4. Base URL

```bash
http://localhost:8080/SmartCampus/api/v1
```

---

## API Endpoints

### Discovery

```http
GET /api/v1
```

Returns API metadata and available resources.

---

### Rooms

#### Get all rooms

```http
GET /api/v1/rooms
```

#### Create a room

```http
POST /api/v1/rooms
Content-Type: application/json
```

```json
{
  "id": "LIB-301",
  "name": "Library Study Room",
  "capacity": 50
}
```

#### Get room by ID

```http
GET /api/v1/rooms/{id}
```

#### Delete room

```http
DELETE /api/v1/rooms/{id}
```

Cannot delete if sensors exist → `409 Conflict`

---

### Sensors

#### Get all sensors

```http
GET /api/v1/sensors
```

#### Filter sensors

```http
GET /api/v1/sensors?type=CO2
```

#### Create sensor

```http
POST /api/v1/sensors
Content-Type: application/json
```

```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 0,
  "roomId": "LIB-301"
}
```

Invalid room → `422 Unprocessable Entity`

---

### Sensor Readings (Sub-resource)

#### Get readings

```http
GET /api/v1/sensors/{id}/readings
```

#### Add reading

```http
POST /api/v1/sensors/{id}/readings
Content-Type: application/json
```

```json
{
  "id": "r1",
  "timestamp": 1710000000000,
  "value": 25.5
}
```

If sensor is in MAINTENANCE → `403 Forbidden`

---

## Error Handling

| Scenario               | Status Code               |
| ---------------------- | ------------------------- |
| Room has sensors       | 409 Conflict              |
| Invalid room reference | 422 Unprocessable Entity  |
| Sensor unavailable     | 403 Forbidden             |
| Unexpected error       | 500 Internal Server Error |

---

## Sample curl Commands

### Create Room

```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"LIB-301","name":"Library","capacity":50}'
```

---

### Create Sensor

```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-1","type":"Temperature","status":"ACTIVE","currentValue":0,"roomId":"LIB-301"}'
```

---

### Get Sensors

```bash
curl http://localhost:8080/SmartCampus/api/v1/sensors
```

---

### Add Reading

```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/TEMP-1/readings \
-H "Content-Type: application/json" \
-d '{"id":"r1","timestamp":1710000000000,"value":22.5}'
```

---

### Delete Room (will fail if sensors exist)

```bash
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms/LIB-301
```

---

## Tech Stack

* Java EE 8 (javax)
* JAX-RS (Jersey)
* Apache Tomcat 9
* Maven
