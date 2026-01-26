package com.juanperuzzo.flappynaruu.controller;

import com.juanperuzzo.flappynaruu.controller.request.SaveScoreRequest;
import com.juanperuzzo.flappynaruu.controller.response.ScoreResponse;
import com.juanperuzzo.flappynaruu.mapper.GameSessionMapper;
import com.juanperuzzo.flappynaruu.service.GameSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@Tag(name = "Leaderboard", description = "Endpoints related to leaderboard and highscores")
public class GameSessionController {

    private final GameSessionService service;

    public GameSessionController(GameSessionService service) {
        this.service = service;
    }

    @Operation(
            summary = "Save a score",
            description = "Registers a new score if it qualifies as a highscore"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Score saved as highscore"),
            @ApiResponse(responseCode = "204", description = "Score did not reach highscore"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<Void> saveScore(@RequestBody @Valid SaveScoreRequest request) {

        boolean saved = service.registerScoreIfHighscore(request.nickname(), request.score());

        return saved ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Get top 5 highscores",
            description = "Returns the top 5 highest scores ordered by score descending"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Top 5 highscores returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScoreResponse.class)
                    )
            )
    })
    @GetMapping("/top5")
    public ResponseEntity<List<ScoreResponse>> top5() {

        List<ScoreResponse> response = service.getTop5().stream()
                .map(GameSessionMapper::toScoreResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}