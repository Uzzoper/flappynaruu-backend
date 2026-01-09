package com.juanperuzzo.flappynaruu.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Leaderboard score response")
public record ScoreResponse(Long id,
                            @Schema(example = "Uzzoper", description = "Player nickname")
                            String nickname,
                            @Schema(example = "12", description = "Player score")
                            Integer score,
                            Instant createdAt
) {
}