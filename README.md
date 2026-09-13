# Fitness Tracker API

A backend REST API for a fitness tracking app — food logging, exercise logging, weight tracking, and nutrition/weight analytics. Built to practice production-style backend engineering: authentication, ownership-based authorization, validation, layered architecture, testing, and containerization.

## Features

- **Auth**: registration and login with JWT-based authentication, BCrypt password hashing, unique usernames
- **Food tracking**: log foods (saved or one-off), log food entries, daily and weekly nutrition summaries vs. personal targets
- **Exercise tracking**: exercise catalog (by muscle group), workout logging (sets/reps/weight)
- **Weight tracking**: weight entries with trend analysis (start/end weight, change, per-day averages)
- **Ownership enforcement**: every resource is scoped to its owner; cross-user access is rejected at the API level, not just the UI
- **Validation & error handling**: consistent, structured JSON error responses for invalid input, auth failures, and ownership violations

## Tech Stack

- **Java 21** / **Spring Boot 4**
- **Spring Data JPA** / **Hibernate** — ORM
- **PostgreSQL** — database
- **Spring Security** + **JWT (jjwt)** — authentication
- **Docker** / **Docker Compose** — containerization
- **JUnit 5** — unit testing for aggregation/business logic
- **Maven** — build tool

## Architecture

Standard layered structure:

```
Controller -> Service (business logic) -> Repository (data access) -> PostgreSQL
```

- **Entities** map directly to database tables via JPA, with real relationships (`@ManyToOne`) rather than raw foreign key IDs.
- **DTOs** define the API's actual contract, decoupled from the database schema — response DTOs never expose sensitive fields (e.g. password hashes), and flatten related data where it reduces round-trips for a client.
- **Service layer** holds pure, testable business logic (e.g. nutrition aggregation math) separated from HTTP and persistence concerns.
- **JWT authentication filter** verifies tokens on every request; a custom `AuthenticationEntryPoint` and `AccessDeniedHandler` return clean JSON errors instead of default framework output.

## Running Locally

**Requirements:** Docker Desktop.

```bash
docker-compose up
```

This builds the app image and starts both the API and a PostgreSQL container, fully networked. The API is available at `http://localhost:8080`.

## Example API Usage

**Register**

```bash
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d "{\"username\":\"jdoe\",\"password\":\"yourpassword\"}"
```

**Login**

```bash
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"username\":\"jdoe\",\"password\":\"yourpassword\"}"
```

Returns a JWT. Include it on all subsequent requests:

```
Authorization: Bearer <token>
```

**Log a food entry**

```bash
curl -X POST http://localhost:8080/food-entries -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d "{\"food\":{\"foodId\":\"<food-id>\"},\"quantity\":2,\"time\":\"2026-09-13\"}"
```

**Daily nutrition summary**

```bash
curl "http://localhost:8080/food-entries/daily-summary?date=2026-09-13" -H "Authorization: Bearer <token>"
```

## Endpoints

| Resource | Endpoints |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login` |
| Users | `GET/PUT/DELETE /users/{id}` |
| Foods | `POST/GET/PUT/DELETE /foods`, `/foods/{id}` |
| Food Entries | `POST/GET/PUT/DELETE /food-entries`, `/food-entries/{id}` |
| | `GET /food-entries/daily-summary?date=` |
| | `GET /food-entries/weekly-summary?weekStart=&weekEnd=` |
| Exercises | `POST/GET/PUT/DELETE /exercises`, `/exercises/{id}` |
| Exercise Entries | `POST/GET/PUT/DELETE /exercise-entries`, `/exercise-entries/{id}` |
| Weight Entries | `POST/GET/PUT/DELETE /weight-entries`, `/weight-entries/{id}` |
| | `GET /weight-entries/weight-trends?startDate=&endDate=` |

## Testing

```bash
./mvnw test
```

Unit tests cover the aggregation/business logic in the service layer, run in isolation with no database dependency.

## Notes

This is a portfolio/learning project focused on backend fundamentals. There is currently no frontend — the API is fully functional and testable via any HTTP client (Postman, curl, etc.).