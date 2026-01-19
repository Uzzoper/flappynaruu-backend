# Flappynaruu – Backend API

*Flappy Naruu Backend* is a lightweight, RESTful API built with *Spring Boot*, responsible for managing and exposing the *Top 5 highscores* of the Flappy Naruu game.

---

## Features

- Register player scores
- Automatically persists scores only if they are high enough for the *Top 5*
- Retrieve the *Top 5 leaderboard*
- Centralized exception handling
- *OpenAPI / Swagger* documentation
- Unit and controller tests for learning and reliability

---

## Technical Stack

This backend leverages modern Java and Spring technologies:

- *Java 17*
- *Spring Boot 3.5.9*
- *Spring Web*
- *Spring Data JPA*
- *PostgreSQL*
- *Springdoc OpenAPI (Swagger)*
- *JUnit 5 & Mockito*

---

## Architecture Overview

The project follows a layered architecture:

controller → service → repository → database

## API Documentation (Swagger)

Once the application is running, access the API documentation at:

http://localhost:8080/swagger-ui.html

---

## Endpoints

### Save Score

*POST /leaderboard*

Registers a new score only if it qualifies as a highscore.

**Request body:**
```json
{
  "nickname": "Player1",
  "score": 120
}
```

Responses:

201 Created → score saved as a highscore

204 No Content → score did not qualify

400 Bad Request → invalid nickname or score

---

### Get Top 5 Leaderboard

*GET /leaderboard/top5*

Returns the current Top 5 highscores.

Response:
```json
[
  {
    "nickname": "Player1",
    "score": 300,
    "createdAt": "2026-01-09T14:30:00Z"
  }
]
```

---

## Testing Strategy

This project includes tests focused on learning best practices:

### Service tests

- Business rules

- Highscore replacement logic

### Controller tests

- HTTP status validation

- Request/response serialization

- Service mocking with Mockito

---

## License

This project is licensed under the GNU GPLv3.
See the LICENSE file for full details.

---

## Author

Developed by Juan Antonio Peruzzo

GitHub: @Uzzoper