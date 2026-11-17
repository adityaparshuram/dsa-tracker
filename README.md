# DSA Tracker

Full-stack tracker for data-structure and algorithm practice: log problems, filter by topic/difficulty/status,
keep approach notes, and see your streak, topic coverage and a 12-week activity map.

**Stack:** Java 17, Spring Boot 3 (Web, Data JPA, Validation), MySQL 8, HTML, CSS, vanilla JavaScript.

## Run
1. Install JDK 17+, Maven, MySQL 8.
2. Edit `src/main/resources/application.properties` (DB username/password). The database is created automatically.
3. `mvn spring-boot:run`
4. Open http://localhost:8080
(Optional: run `schema.sql` in MySQL to create the table manually and load sample rows.)

## REST API
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/problems?topic=&difficulty=&status=&q=` | List with optional filters |
| POST | `/api/problems` | Create |
| PUT | `/api/problems/{id}` | Update |
| DELETE | `/api/problems/{id}` | Delete |
| GET | `/api/stats` | Totals, per-topic/difficulty counts, streak, activity map |

## Architecture
`controller` (REST) → `service` (business rules, analytics) → `repository` (Spring Data JPA, JPQL filter query) → MySQL.
Bean Validation on the entity, central `@RestControllerAdvice` for clean JSON errors. Solving a problem auto-stamps the solved date.

## Resume bullets
- Built a full-stack DSA practice tracker with Spring Boot REST APIs, JPA/Hibernate and MySQL, with dynamic multi-filter search.
- Implemented streak and activity analytics (grouping, streak algorithm) served via a dashboard API.
- Designed a responsive, dependency-free JS frontend with an activity heatmap, validation feedback and dark mode.

## Ideas to extend
JWT login (multi-user), spaced-repetition revision schedule, CSV import/export, Docker Compose, JUnit/MockMvc tests.
