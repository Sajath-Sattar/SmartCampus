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

---

## Answers to the coursework questions

### 1.	In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

By default, JAX-RS resource classes are **request-scoped**, meaning a **new instance is created for each incoming HTTP request**.

This ensures:

* Thread safety by default
* No shared mutable state between requests

However, since the application uses **in-memory data structures (e.g., HashMap)** stored in static classes, these are shared across requests. This introduces potential issues such as:

* Race conditions
* Data inconsistency in concurrent access scenarios

To mitigate this, synchronization or concurrent collections (e.g., `ConcurrentHashMap`) should be used in production systems. In this coursework, basic structures are sufficient due to its simplified scope.

### 2. Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?
Hypermedia (HATEOAS) allows API responses to include **links to related resources**, enabling clients to dynamically navigate the API.

Benefits:

* Reduces dependency on external documentation
* Improves API discoverability
* Allows APIs to evolve without breaking clients

For example, a discovery endpoint providing links like `/rooms` and `/sensors` helps clients understand available operations without hardcoding URLs.

### 3. When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.
Returning only IDs:

* Reduces response size (better performance)
* Requires additional requests from clients

Returning full objects:

* Provides complete data in one response
* Increases payload size

Trade-off:

* IDs → efficient for large datasets
* Full objects → convenient for clients

In this API, full objects are returned for simplicity and usability.

### 4. Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.
Yes, DELETE is **idempotent**.

If a DELETE request is sent multiple times:

* First request removes the resource
* Subsequent requests do not change the system state

In this implementation:

* First DELETE removes the room
* Repeated DELETE calls have no further effect (resource already gone)

Thus, the outcome remains consistent, satisfying idempotency.

### 5. We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?
The API uses:

```java
@Consumes(MediaType.APPLICATION_JSON)
```

If a client sends:

* `text/plain` or `application/xml`

JAX-RS will:

* Reject the request
* Return **HTTP 415 Unsupported Media Type**

This ensures strict validation of input format and prevents incorrect data parsing.

### 6. You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?
Using query parameters (e.g., `/sensors?type=CO2`) is preferred because:

* It represents **filtering on a collection**
* Supports multiple optional filters easily
* Keeps URL structure clean and flexible

Using path (e.g., `/sensors/type/CO2`):

* Implies a fixed hierarchy
* Becomes messy with multiple filters

Thus, query parameters are more suitable for search and filtering operations.

### 7. Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?
The Sub-Resource Locator pattern allows delegating nested resource handling to separate classes.

Benefits:

* Improves code modularity
* Reduces complexity in main resource classes
* Enhances readability and maintainability

Instead of handling:

```
/sensors/{id}/readings
```

in one large class, logic is separated into `SensorReadingResource`, making the API cleaner and scalable.

### 8. Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?
HTTP 422 (Unprocessable Entity) is more appropriate because:

* The request is syntactically correct
* But contains invalid data (e.g., non-existent `roomId`)

Using 404 would imply the endpoint itself is missing, which is incorrect.

Thus:

* **422 → invalid data inside request**
* **404 → resource/endpoint not found**

### 9. From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?
Exposing stack traces is a security risk because it reveals:

* Internal class names and structure
* Framework and library details
* File paths and system information

Attackers can use this information to:

* Identify vulnerabilities
* Plan targeted attacks

Therefore, APIs should return **generic error messages** (e.g., HTTP 500) instead of raw stack traces.

### 10. Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?
Using filters for logging is better than adding logging in each method because:

* Centralized logging logic
* Avoids code duplication
* Ensures consistency across all endpoints
* Easier to maintain and modify

Filters act as cross-cutting components that automatically intercept all requests and responses.
