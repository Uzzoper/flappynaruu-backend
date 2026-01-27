package com.juanperuzzo.flappynaruu.service;

import com.juanperuzzo.flappynaruu.entity.GameSession;
import com.juanperuzzo.flappynaruu.repository.GameSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameSessionService {

    private final GameSessionRepository repository;

    public GameSessionService(GameSessionRepository repository) {
        this.repository = repository;
    }

    public boolean registerScoreIfHighscore(String nickname, Integer score) {

        List<GameSession> top5 = repository.findTop5ByOrderByScoreDescCreatedAtAsc();

        if (top5.size() < 5) {
            repository.save(new GameSession(nickname, score));
            return true;
        }

        GameSession lowest = top5.get(4);

        if (score > lowest.getScore()) {
            repository.delete(lowest);
            repository.save(new GameSession(nickname, score));
            return true;
        }
        return false;
    }

    public List<GameSession> getTop5() {
        return repository.findTop5ByOrderByScoreDescCreatedAtAsc();
    }
}