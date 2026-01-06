package com.juanperuzzo.flappynaruu.service;

import com.juanperuzzo.flappynaruu.entity.GameSession;
import com.juanperuzzo.flappynaruu.repository.GameSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class GameSessionService {

    private final GameSessionRepository repository;

    public GameSessionService(GameSessionRepository repository) {
        this.repository = repository;
    }

    public GameSession createGameSession (String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be empty");
        }

        GameSession gameSession = new GameSession(nickname);
        return repository.save(gameSession);
    }
}