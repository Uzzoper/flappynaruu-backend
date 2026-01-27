package com.juanperuzzo.flappynaruu.repository;

import com.juanperuzzo.flappynaruu.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    List<GameSession> findTop5ByOrderByScoreDescCreatedAtAsc();
}