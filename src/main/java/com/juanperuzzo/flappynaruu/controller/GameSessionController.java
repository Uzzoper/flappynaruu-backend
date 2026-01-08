package com.juanperuzzo.flappynaruu.controller;

import com.juanperuzzo.flappynaruu.controller.request.SaveScoreRequest;
import com.juanperuzzo.flappynaruu.controller.response.ScoreResponse;
import com.juanperuzzo.flappynaruu.mapper.GameSessionMapper;
import com.juanperuzzo.flappynaruu.service.GameSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class GameSessionController {

    private final GameSessionService service;

    public GameSessionController(GameSessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> saveScore(@RequestBody SaveScoreRequest request) {

        boolean saved = service.registerScoreIfHighscore(request.nickname(), request.score());

        return saved ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/top5")
    public ResponseEntity<List<ScoreResponse>> top5() {

        List<ScoreResponse> response = service.getTop5().stream()
                .map(GameSessionMapper::toScoreResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}