package com.juanperuzzo.flappynaruu.mapper;

import com.juanperuzzo.flappynaruu.controller.response.ScoreResponse;
import com.juanperuzzo.flappynaruu.entity.GameSession;

public class GameSessionMapper {

    public GameSessionMapper() {
    }

    public static ScoreResponse toScoreResponse(GameSession session) {
        return new ScoreResponse(
                session.getId(),
                session.getNickname(),
                session.getScore(),
                session.getCreatedAt()
        );
    }
}