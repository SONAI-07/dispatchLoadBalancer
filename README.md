# 🚚 Fleet Dispatch Load Balancer

An enterprise-grade, priority-driven fleet routing and load-balancing backend built with **Java** and **Spring Boot**.

This system dynamically assigns incoming logistics orders to a fleet of vehicles by optimizing for geographic proximity (using the Haversine formula) while strictly enforcing capacity limits and a fixed-radius hub-and-spoke routing model.

## 🌟 Core Architectural Features

* **Priority-First Assignment:** Orders are strictly processed by priority (`HIGH` > `MEDIUM` > `LOW`) ensuring critical cargo always gets first access to the fleet.
* **Geographic Hub-and-Spoke Constraint:** Implements a strict 10km maximum search radius from a vehicle's fixed starting location, preventing the "Creeping Radius" problem and optimizing fuel efficiency.
* **Capacity Maximization (Knapsack Resolution):** Vehicles remain in the available pool until they are mathematically full. A single vehicle can pick up multiple orders of varying priorities as long as the total weight does not exceed its `maxCapacity`.
* **Partial Fulfillment Pattern:** Utilizes transactional safety. If a specific order cannot be assigned (due to fleet capacity fragmentation or distance limits), the system gracefully skips it, commits the successful assignments, and reports the skipped orders in the API response without rolling back the database.
* **Robust Exception Handling:** Implements `@RestControllerAdvice` to capture malformed JSON and internal server errors, returning clean, standardized HTTP responses.

## 🛠 Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **Database:** PostgreSQL@18 (with PostGIS considerations) / Spring Data JPA
* **Testing:** JUnit 5 (Jupiter), Mockito
* **Build Tool:** Maven
* **Utilities:** Lombok, Haversine Geographic Math

## 🚀 Getting Started

### Prerequisites
* Java Development Kit (JDK) 17 or higher
* Maven installed
* PostgreSQL database running locally or via Docker. *(Tip: If you need a quick local setup on macOS, use Homebrew: `brew install postgresql`)*

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/SONAI-07/dispatchLoadBalancer.git

* 2.Configure the Database:
 ```bash
     spring.datasource.url=jdbc:postgresql://localhost:5432/dispatch
spring.datasource.username=archanbanerjee
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```
* 3.Build and Run:
```bash
  mvn clean install
  mvn spring-boot:run
```

## 📖 API Documentation
### 1. Register a New Vehicle
**Endpoint:** `POST /api/dispatch/vehicles`
**Request Body:**
```json
{
  "vehicles":[
  {
  "vehicleId": "VEH001",
  "maxCapacity": 100.0,
  "currentLatitude": 12.9716,
  "currentLongitude": 77.6413,
  "currentAddress":"Bangalore"
},

{
  "vehicleId": "VEH002",
  "maxCapacity": 150.0,
  "currentLatitude": 13.0674,
  "currentLongitude": 77.6413,
  "currentAddress": "Chennai"
}
    ]}

```

**Response :**
```json
{
"message": "Vehicle details accepted.",
"status": "success"
}
```

### 2. Create a New Order

**Endpoint:** POST /api/dispatch/orders
```json
{
  "orders": [
    {
  "orderId": "ORD001",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "address": "Connaught Place, New Delhi",
  "packageWeight": 80.0,
  "priority": "HIGH"
},

{
  "orderId": "ORD002",
  "latitude": 13.0827,
  "longitude": 80.2707,
  "address": "Anna Salai,Chennai",
  "packageWeight": 20.0,
  "priority": "MEDIUM"
}
]}

```
**Response :**
```json
{
"message": "Orders details accepted.",
"status": "success"
}
```

### 3.Optimize and Retrieve Dispatch Plan

**Endpoint:** GET /api/dispatch/plan

```json
{
  "dispatch plan": [
    {
      "vehicleId": "VEH001",
      "totalLoad": 80,
      "totalDistance": "5km"
    {
      "orderId": "ORD001",
      "latitude": 12.9716,
      "longitude": 77.5946,
      "address": "Connaught Place, New Delhi",
      "packageWeight": 80.0,
      "priority": "HIGH"
    }
    
    },
  
    
    {
  "vehicleId": "VEH002",
   "totalLoad": 20,
   "totalDistance": "6km"

{
  "orderId": "ORD002",
  "latitude": 13.0827,
  "longitude": 80.2707,
  "address": "Anna Salai,Chennai",
  "packageWeight": 20.0,
  "priority": "MEDIUM"
  }
 
  }
    ]
}
 ```
## 🧪 Testing
This project emphasizes Test-Driven Development (TDD) with comprehensive unit tests ensuring mathematical accuracy and business logic resilience.

```json
    mvn test
```