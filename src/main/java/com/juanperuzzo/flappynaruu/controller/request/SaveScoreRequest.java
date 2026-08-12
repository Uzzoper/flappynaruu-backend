package com.juanperuzzo.flappynaruu.controller.request;

import com.juanperuzzo.flappynaruu.validation.NoBadWords;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Leaderboard request")
public record SaveScoreRequest(@Schema(example = "Uzzoper", description = "Player nickname")
                               @NotBlank(message = "Nickname não pode ser vazio")
                               @Size(max = 30, message = "Nickname não pode ser tão longo")
                               @NoBadWords(message = "Nickname não pode conter palavras inapropriadas")
                               String nickname,
                               @Schema(example = "12", description = "Player score")
                               @Min(value = 1, message = "Score deve ser maior que zero")
                               @Max(value = 1000000, message = "Score acima do máximo permitido")
                               Integer score,
                               @Schema(example = "1700000000000", description = "Submission timestamp in epoch millis")
                               @NotNull(message = "Timestamp é obrigatório")
                               Long timestamp,
                               @Schema(example = "a1b2c3...", description = "HMAC-SHA256 signature of nickname:score:timestamp")
                               @NotBlank(message = "Assinatura é obrigatória")
                               String signature) {
}