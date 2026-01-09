package com.juanperuzzo.flappynaruu.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Leaderboard request")
public record SaveScoreRequest(@Schema(example = "Uzzoper", description = "Player nickname")
                               String nickname,
                               @Schema(example = "12", description = "Player score")
                               Integer score
) {
}