package com.juanperuzzo.flappynaruu.controller;

import com.juanperuzzo.flappynaruu.controller.request.CreateGameSessionRequest;
import com.juanperuzzo.flappynaruu.controller.response.CreateGameSessionResponse;
import com.juanperuzzo.flappynaruu.entity.GameSession;
import com.juanperuzzo.flappynaruu.service.GameSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game-session")
public class GameSessionController {

    private final GameSessionService service;

    public GameSessionController(GameSessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateGameSessionResponse> create(@RequestBody CreateGameSessionRequest request) {
        GameSession session = service.createGameSession(request.nickname());

        CreateGameSessionResponse response = new CreateGameSessionResponse(
                session.getId(),
                session.getNickname(),
                session.getScore(),
                session.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}