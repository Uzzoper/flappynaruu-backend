package com.juanperuzzo.flappynaruu.controller.response;

import java.time.Instant;

public record CreateGameSessionResponse(Long id,
                                        String nickname,
                                        Integer score,
                                        Instant createdAt
) {
}